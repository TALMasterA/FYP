/**
 * Unit tests for translation.ts — translation and language detection Cloud Functions.
 */
// ── Mock setup ────────────────────────────────────────────────────────

const mockFetch = jest.fn();
jest.mock("node-fetch", () => ({
  __esModule: true,
  default: mockFetch,
}));

jest.mock("firebase-admin", () => ({
  firestore: jest.fn(() => ({})),
  initializeApp: jest.fn(),
}));

jest.mock("firebase-functions/params", () => ({
  defineSecret: jest.fn((name: string) => ({name, value: () => "mock-value"})),
}));

jest.mock("firebase-functions/v2/https", () => {
  const actual = jest.requireActual("firebase-functions/v2/https");
  return {
    ...actual,
    onCall: jest.fn((opts: any, handler?: any) => handler || opts),
  };
});

// ── Import after mocks ────────────────────────────────────────────────

import {
  getSpeechToken,
  translateText,
  translateTexts,
  detectLanguage,
} from "../translation.js";

beforeEach(() => jest.clearAllMocks());

// Helper to create a mock fetch response
function mockResponse(body: string, ok = true, status = 200) {
  return {ok, status, text: async () => body, statusText: "OK"};
}

// ── getSpeechToken ────────────────────────────────────────────────────

describe("getSpeechToken", () => {
  it("rejects unauthenticated requests", async () => {
    await expect(
      (getSpeechToken as any)({auth: null})
    ).rejects.toThrow("Login required.");
  });

  it("returns token and region on success", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("test-token-123"));
    const result = await (getSpeechToken as any)({auth: {uid: "u1"}});
    expect(result).toEqual({token: "test-token-123", region: "mock-value"});
  });

  it("throws on API error", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("error", false, 500));
    await expect(
      (getSpeechToken as any)({auth: {uid: "u1"}})
    ).rejects.toThrow("Speech service unavailable");
  });
});

// ── translateText ─────────────────────────────────────────────────────

describe("translateText", () => {
  it("rejects unauthenticated requests", async () => {
    await expect(
      (translateText as any)({auth: null, data: {text: "hi", to: "en"}})
    ).rejects.toThrow("Login required.");
  });

  it("rejects text exceeding max length", async () => {
    const longText = "a".repeat(5001);
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: longText, to: "en"}})
    ).rejects.toThrow("exceeds maximum length");
  });

  it("rejects missing text", async () => {
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "", to: "en"}})
    ).rejects.toThrow("text is required");
  });

  it("rejects missing target language", async () => {
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hello", to: ""}})
    ).rejects.toThrow("to is required");
  });

  it("returns translated text on success", async () => {
    const apiResponse = JSON.stringify([{translations: [{text: "hola"}]}]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (translateText as any)({
      auth: {uid: "u1"},
      data: {text: "hello", to: "es"},
    });
    expect(result).toEqual({translatedText: "hola"});
  });

  it("throws on API error", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("error", false, 500));
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hi", to: "en"}})
    ).rejects.toThrow("Translation service unavailable");
  });
});

// ── translateTexts ────────────────────────────────────────────────────

describe("translateTexts", () => {
  it("rejects batch exceeding max count", async () => {
    const texts = Array(801).fill("hi");
    await expect(
      (translateTexts as any)({auth: {uid: "u1"}, data: {texts, to: "en"}})
    ).rejects.toThrow("Too many texts");
  });

  it("returns translated texts on success", async () => {
    const apiResponse = JSON.stringify([
      {translations: [{text: "hola"}]},
      {translations: [{text: "mundo"}]},
    ]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (translateTexts as any)({
      auth: {uid: "u1"},
      data: {texts: ["hello", "world"], to: "es"},
    });
    expect(result).toEqual({translatedTexts: ["hola", "mundo"]});
  });

  it("handles empty texts array", async () => {
    const apiResponse = JSON.stringify([]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (translateTexts as any)({
      auth: {uid: "u1"},
      data: {texts: [], to: "en"},
    });
    expect(result).toEqual({translatedTexts: []});
  });
});

// ── detectLanguage ────────────────────────────────────────────────────

describe("detectLanguage", () => {
  it("rejects unauthenticated requests", async () => {
    await expect(
      (detectLanguage as any)({auth: null, data: {text: "hello"}})
    ).rejects.toThrow("Login required.");
  });

  it("returns detected language on success", async () => {
    const apiResponse = JSON.stringify([{
      language: "en",
      score: 1.0,
      isTranslationSupported: true,
      alternatives: [{language: "de", score: 0.5}],
    }]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (detectLanguage as any)({
      auth: {uid: "u1"},
      data: {text: "hello world"},
    });
    expect(result).toEqual({
      language: "en",
      score: 1.0,
      isTranslationSupported: true,
      alternatives: [{language: "de", score: 0.5}],
    });
  });

  it("provides defaults for missing detection fields", async () => {
    const apiResponse = JSON.stringify([{}]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (detectLanguage as any)({
      auth: {uid: "u1"},
      data: {text: "???"},
    });
    expect(result).toEqual({
      language: "",
      score: 0,
      isTranslationSupported: false,
      alternatives: [],
    });
  });
});
