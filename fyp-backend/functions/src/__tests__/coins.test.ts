/**
 * Unit tests for coins.ts — quiz coin awarding and shop purchases.
 */
// ── Firestore mock setup ──────────────────────────────────────────────

const mockTxGet = jest.fn();
const mockTxSet = jest.fn();
const mockRunTransaction = jest.fn(async (cb: any) => {
  return cb({get: mockTxGet, set: mockTxSet});
});

// Recursive mock: collection().doc().collection().doc()...
function makeDocRef(): any {
  return {collection: makeCollectionRef};
}
function makeCollectionRef(): any {
  return {doc: jest.fn(makeDocRef)};
}

jest.mock("firebase-admin", () => ({
  firestore: Object.assign(
    jest.fn(() => ({
      collection: jest.fn(makeCollectionRef),
      runTransaction: mockRunTransaction,
      batch: jest.fn(() => ({delete: jest.fn(), commit: jest.fn()})),
    })),
    {
      FieldValue: {
        serverTimestamp: jest.fn(() => "mock-timestamp"),
        arrayUnion: jest.fn((...args: any[]) => args),
      },
    }
  ),
  initializeApp: jest.fn(),
}));
jest.mock("firebase-functions/params", () => ({
  defineSecret: jest.fn((name: string) => ({name, value: () => "mock"})),
}));

// Mock onCall to extract the handler
let awardHandler: any;
let spendHandler: any;
jest.mock("firebase-functions/v2/https", () => {
  const actual = jest.requireActual("firebase-functions/v2/https");
  return {
    ...actual,
    onCall: jest.fn((opts: any, handler?: any) => {
      const fn = handler || opts;
      return fn;
    }),
  };
});

// ── Import after mocks ────────────────────────────────────────────────

import {awardQuizCoins, spendCoins} from "../coins.js";

beforeEach(() => {
  awardHandler = awardQuizCoins;
  spendHandler = spendCoins;
  jest.clearAllMocks();
});

// ── awardQuizCoins ────────────────────────────────────────────────────

describe("awardQuizCoins", () => {
  const validData = {
    attemptId: "attempt-1",
    primaryLanguageCode: "en",
    targetLanguageCode: "zh-HK",
    generatedHistoryCountAtGenerate: 30,
    totalScore: 8,
  };

  it("rejects unauthenticated requests", async () => {
    await expect(
      awardHandler({auth: null, data: validData})
    ).rejects.toThrow("Login required.");
  });

  it("rejects missing attemptId", async () => {
    await expect(
      awardHandler({auth: {uid: "u1"}, data: {...validData, attemptId: ""}})
    ).rejects.toThrow("attemptId is required");
  });

  it("rejects negative totalScore", async () => {
    await expect(
      awardHandler({auth: {uid: "u1"}, data: {...validData, totalScore: -1}})
    ).rejects.toThrow("totalScore must be a non-negative number");
  });

  it("rejects totalScore exceeding MAX_QUIZ_SCORE (50)", async () => {
    await expect(
      awardHandler({auth: {uid: "u1"}, data: {...validData, totalScore: 51}})
    ).rejects.toThrow("totalScore exceeds maximum of 50");
  });

  it("rejects non-positive generatedHistoryCountAtGenerate", async () => {
    await expect(
      awardHandler({auth: {uid: "u1"}, data: {...validData, generatedHistoryCountAtGenerate: 0}})
    ).rejects.toThrow("generatedHistoryCountAtGenerate must be a positive number");
  });

  it("rejects invalid language code format", async () => {
    await expect(
      awardHandler({auth: {uid: "u1"}, data: {...validData, primaryLanguageCode: "en; DROP TABLE"}})
    ).rejects.toThrow("Language codes must be");
  });

  it("returns zero_score when totalScore is 0", async () => {
    const result = await awardHandler({
      auth: {uid: "u1"},
      data: {...validData, totalScore: 0},
    });
    expect(result).toEqual({awarded: false, reason: "zero_score"});
  });

  it("returns already_awarded when coin_awards doc exists", async () => {
    mockTxGet.mockResolvedValueOnce({exists: true}); // coin_awards doc exists
    const result = await awardHandler({auth: {uid: "u1"}, data: validData});
    expect(result).toEqual({awarded: false, reason: "already_awarded"});
  });

  it("returns no_sheet when learning sheet does not exist", async () => {
    mockTxGet
      .mockResolvedValueOnce({exists: false}) // coin_awards - not exists
      .mockResolvedValueOnce({exists: false}); // sheet - not exists
    const result = await awardHandler({auth: {uid: "u1"}, data: validData});
    expect(result).toEqual({awarded: false, reason: "no_sheet"});
  });

  it("returns version_mismatch when sheet version differs", async () => {
    mockTxGet
      .mockResolvedValueOnce({exists: false}) // coin_awards
      .mockResolvedValueOnce({exists: true, data: () => ({historyCountAtGenerate: 20})}) // sheet (version 20 != 30)
    const result = await awardHandler({auth: {uid: "u1"}, data: validData});
    expect(result).toEqual({awarded: false, reason: "version_mismatch"});
  });

  it("returns insufficient_records when not enough new records since last award", async () => {
    mockTxGet
      .mockResolvedValueOnce({exists: false}) // coin_awards
      .mockResolvedValueOnce({exists: true, data: () => ({historyCountAtGenerate: 30})}) // sheet
      .mockResolvedValueOnce({exists: true, data: () => ({count: 25})}); // last_awarded (25 + 10 = 35 > 30)
    const result = await awardHandler({auth: {uid: "u1"}, data: validData});
    expect(result).toEqual({awarded: false, reason: "insufficient_records", needed: 5});
  });

  it("awards coins when all checks pass (first quiz)", async () => {
    mockTxGet
      .mockResolvedValueOnce({exists: false}) // coin_awards
      .mockResolvedValueOnce({exists: true, data: () => ({historyCountAtGenerate: 30})}) // sheet
      .mockResolvedValueOnce({exists: false}) // last_awarded (first quiz)
      .mockResolvedValueOnce({exists: true, data: () => ({coinTotal: 10, coinByLang: {}})}); // coin stats
    const result = await awardHandler({auth: {uid: "u1"}, data: validData});
    expect(result).toEqual({awarded: true, coinsAwarded: 8, newTotal: 18});
    expect(mockTxSet).toHaveBeenCalledTimes(3); // coin stats + coin award + last awarded
  });

  it("awards coins with existing last_awarded when increment is sufficient", async () => {
    mockTxGet
      .mockResolvedValueOnce({exists: false}) // coin_awards
      .mockResolvedValueOnce({exists: true, data: () => ({historyCountAtGenerate: 30})}) // sheet
      .mockResolvedValueOnce({exists: true, data: () => ({count: 20})}) // last_awarded (20 + 10 = 30 <= 30, OK)
      .mockResolvedValueOnce({exists: false}); // coin stats (new user)
    const result = await awardHandler({auth: {uid: "u1"}, data: validData});
    expect(result).toEqual({awarded: true, coinsAwarded: 8, newTotal: 8});
  });
});

// ── spendCoins ────────────────────────────────────────────────────────

describe("spendCoins", () => {
  it("rejects unauthenticated requests", async () => {
    await expect(
      spendHandler({auth: null, data: {purchaseType: "history_expansion"}})
    ).rejects.toThrow("Login required.");
  });

  it("rejects invalid purchaseType", async () => {
    await expect(
      spendHandler({auth: {uid: "u1"}, data: {purchaseType: "free_stuff"}})
    ).rejects.toThrow("purchaseType must be");
  });

  it("rejects palette_unlock with invalid paletteId", async () => {
    await expect(
      spendHandler({auth: {uid: "u1"}, data: {purchaseType: "palette_unlock", paletteId: "hacked"}})
    ).rejects.toThrow("Invalid palette ID");
  });

  it("returns max_limit_reached for history expansion at limit", async () => {
    mockTxGet
      .mockResolvedValueOnce({data: () => ({coinTotal: 2000, coinByLang: {}})}) // coin stats
      .mockResolvedValueOnce({data: () => ({historyViewLimit: 60})}); // settings at max
    const result = await spendHandler({
      auth: {uid: "u1"},
      data: {purchaseType: "history_expansion"},
    });
    expect(result).toEqual({success: false, reason: "max_limit_reached"});
  });

  it("returns insufficient_coins for history expansion without enough coins", async () => {
    mockTxGet
      .mockResolvedValueOnce({data: () => ({coinTotal: 500, coinByLang: {}})}) // not enough
      .mockResolvedValueOnce({data: () => ({historyViewLimit: 30})});
    const result = await spendHandler({
      auth: {uid: "u1"},
      data: {purchaseType: "history_expansion"},
    });
    expect(result).toEqual({success: false, reason: "insufficient_coins"});
  });

  it("successfully expands history limit", async () => {
    mockTxGet
      .mockResolvedValueOnce({data: () => ({coinTotal: 1500, coinByLang: {}})})
      .mockResolvedValueOnce({data: () => ({historyViewLimit: 30})});
    const result = await spendHandler({
      auth: {uid: "u1"},
      data: {purchaseType: "history_expansion"},
    });
    expect(result).toEqual({success: true, newBalance: 500, newLimit: 40});
    expect(mockTxSet).toHaveBeenCalledTimes(2);
  });

  it("returns already_unlocked for duplicate palette purchase", async () => {
    mockTxGet
      .mockResolvedValueOnce({data: () => ({coinTotal: 100, coinByLang: {}})})
      .mockResolvedValueOnce({data: () => ({unlockedPalettes: ["default", "ocean"]})});
    const result = await spendHandler({
      auth: {uid: "u1"},
      data: {purchaseType: "palette_unlock", paletteId: "ocean"},
    });
    expect(result).toEqual({success: false, reason: "already_unlocked"});
  });

  it("returns insufficient_coins for palette unlock", async () => {
    mockTxGet
      .mockResolvedValueOnce({data: () => ({coinTotal: 5, coinByLang: {}})})
      .mockResolvedValueOnce({data: () => ({unlockedPalettes: ["default"]})});
    const result = await spendHandler({
      auth: {uid: "u1"},
      data: {purchaseType: "palette_unlock", paletteId: "ocean"},
    });
    expect(result).toEqual({success: false, reason: "insufficient_coins"});
  });

  it("successfully unlocks a palette", async () => {
    mockTxGet
      .mockResolvedValueOnce({data: () => ({coinTotal: 50, coinByLang: {}})})
      .mockResolvedValueOnce({data: () => ({unlockedPalettes: ["default"]})});
    const result = await spendHandler({
      auth: {uid: "u1"},
      data: {purchaseType: "palette_unlock", paletteId: "sunset"},
    });
    expect(result).toEqual({success: true, newBalance: 40});
    expect(mockTxSet).toHaveBeenCalledTimes(2);
  });
});
