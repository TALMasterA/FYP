/**
 * Unit tests for learning.ts quiz version sync trigger.
 */

const mockOnCall = jest.fn((opts: any, handler?: any) => handler || opts);
const mockOnDocumentWritten = jest.fn((opts: any, handler: any) => handler);

const mockFetch = jest.fn();
const mockVersionSet = jest.fn().mockResolvedValue(undefined);
const mockVersionDelete = jest.fn().mockResolvedValue(undefined);
const mockHistoryCountGet = jest.fn();

const mockServerTimestamp = jest.fn(() => "SERVER_TS");

const historyQuery: any = {
  where: jest.fn(() => historyQuery),
  count: jest.fn(() => ({get: mockHistoryCountGet})),
};

const historyCollection = {
  where: jest.fn(() => historyQuery),
};

const quizVersionsCollection = {
  doc: jest.fn(() => ({
    set: mockVersionSet,
    delete: mockVersionDelete,
  })),
};

const userDoc = {
  collection: jest.fn((name: string) => {
    if (name === "history") return historyCollection;
    if (name === "quiz_versions") return quizVersionsCollection;
    throw new Error(`Unexpected user subcollection: ${name}`);
  }),
};

const usersCollection = {
  doc: jest.fn(() => userDoc),
};

const mockFirestore = {
  collection: jest.fn((name: string) => {
    if (name === "users") return usersCollection;
    throw new Error(`Unexpected collection: ${name}`);
  }),
};

jest.mock("node-fetch", () => ({
  __esModule: true,
  default: mockFetch,
}));

jest.mock("firebase-functions/v2/https", () => ({
  HttpsError: class MockHttpsError extends Error {
    code: string;
    constructor(code: string, message: string) {
      super(message);
      this.code = code;
      this.name = "HttpsError";
    }
  },
  onCall: mockOnCall,
}));

jest.mock("firebase-functions/v2/firestore", () => ({
  onDocumentWritten: mockOnDocumentWritten,
}));

jest.mock("firebase-admin", () => ({
  firestore: {
    FieldValue: {
      serverTimestamp: mockServerTimestamp,
    },
  },
}));

jest.mock("../helpers.js", () => ({
  requireAuth: jest.fn(),
  requireString: jest.fn(),
  enforceRateLimit: jest.fn(),
  validateGenAiConfig: jest.fn(),
  getFirestore: jest.fn(() => mockFirestore),
  GENAI_BASE_URL: {value: () => "https://example.invalid"},
  GENAI_API_VERSION: {value: () => "2023-05-15"},
  GENAI_API_KEY: {value: () => "test-key"},
}));

jest.mock("../logger.js", () => ({
  logger: {
    info: jest.fn(),
    error: jest.fn(),
    warn: jest.fn(),
  },
}));

import {syncQuizVersionFromLearningSheet} from "../learning.js";
import {logger} from "../logger.js";

describe("syncQuizVersionFromLearningSheet", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockVersionSet.mockResolvedValue(undefined);
    mockVersionDelete.mockResolvedValue(undefined);
    mockHistoryCountGet.mockReset();
  });

  it("deletes quiz version doc when learning sheet is removed", async () => {
    await (syncQuizVersionFromLearningSheet as any)({
      params: {userId: "u1", sheetId: "en__zh"},
      data: {after: {exists: false}},
    });

    expect(mockVersionDelete).toHaveBeenCalledTimes(1);
    expect(mockVersionSet).not.toHaveBeenCalled();
  });

  it("swallows delete failures when learning sheet is removed", async () => {
    mockVersionDelete.mockRejectedValueOnce(new Error("delete failed"));

    await expect((syncQuizVersionFromLearningSheet as any)({
      params: {userId: "u1", sheetId: "en__zh"},
      data: {after: {exists: false}},
    })).resolves.toBeUndefined();
  });

  it("skips writes and warns for invalid language codes", async () => {
    await (syncQuizVersionFromLearningSheet as any)({
      params: {userId: "u1", sheetId: "bad-sheet-id"},
      data: {
        after: {
          exists: true,
          data: () => ({
            primaryLanguageCode: "english",
            targetLanguageCode: "zh_cn",
          }),
        },
      },
    });

    expect(logger.warn).toHaveBeenCalledWith(
      "syncQuizVersionFromLearningSheet: invalid language codes",
      expect.objectContaining({
        uid: "u1",
        sheetId: "bad-sheet-id",
      })
    );
    expect(mockVersionSet).not.toHaveBeenCalled();
  });

  it("falls back to sheetId language codes and writes merged version metadata", async () => {
    mockHistoryCountGet
      .mockResolvedValueOnce({data: () => ({count: 7})})
      .mockResolvedValueOnce({data: () => ({count: 5})})
      .mockResolvedValueOnce({data: () => ({count: 2})});

    await (syncQuizVersionFromLearningSheet as any)({
      params: {userId: "u1", sheetId: "en__zh"},
      data: {
        after: {
          exists: true,
          data: () => ({
            primaryLanguageCode: "",
            targetLanguageCode: "",
          }),
        },
      },
    });

    expect(mockVersionSet).toHaveBeenCalledWith({
      primaryLanguageCode: "en",
      targetLanguageCode: "zh",
      historyCount: 10,
      sourceSheetId: "en__zh",
      updatedAt: "SERVER_TS",
    }, {merge: true});
  });

  it("logs and exits when history counting throws", async () => {
    mockHistoryCountGet.mockRejectedValueOnce(new Error("count unavailable"));

    await (syncQuizVersionFromLearningSheet as any)({
      params: {userId: "u1", sheetId: "en__zh"},
      data: {
        after: {
          exists: true,
          data: () => ({
            primaryLanguageCode: "en",
            targetLanguageCode: "zh",
          }),
        },
      },
    });

    expect(logger.error).toHaveBeenCalledWith(
      "syncQuizVersionFromLearningSheet: failed to count history",
      expect.objectContaining({
        uid: "u1",
        sheetId: "en__zh",
        targetCode: "zh",
      })
    );
    expect(mockVersionSet).not.toHaveBeenCalled();
  });

  it("treats missing history counts as zero", async () => {
    mockHistoryCountGet
      .mockResolvedValueOnce({data: () => ({})})
      .mockResolvedValueOnce({data: () => ({count: undefined})})
      .mockResolvedValueOnce({data: () => ({count: 99})});

    await (syncQuizVersionFromLearningSheet as any)({
      params: {userId: "u1", sheetId: "en__en"},
      data: {
        after: {
          exists: true,
          data: () => ({
            primaryLanguageCode: "en",
            targetLanguageCode: "en",
          }),
        },
      },
    });

    expect(mockVersionSet).toHaveBeenCalledWith(
      expect.objectContaining({
        historyCount: 0,
      }),
      {merge: true}
    );
  });
});
