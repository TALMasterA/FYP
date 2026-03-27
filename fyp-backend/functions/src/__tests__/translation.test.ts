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
      (translateText as any)({auth: null, data: {text: "hi", to: "en-US"}})
    ).rejects.toThrow("Login required.");
  });

  it("rejects text exceeding max length", async () => {
    const longText = "a".repeat(5001);
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: longText, to: "en-US"}})
    ).rejects.toThrow("exceeds maximum length");
  });

  it("rejects missing text", async () => {
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "", to: "en-US"}})
    ).rejects.toThrow("text is required");
  });

  it("rejects missing target language", async () => {
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hello", to: ""}})
    ).rejects.toThrow("to is required");
  });

  it("rejects invalid target language code", async () => {
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hello", to: "invalid-lang"}})
    ).rejects.toThrow("must be one of:");
  });

  it("rejects invalid source language code", async () => {
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hello", to: "en-US", from: "xyz"}})
    ).rejects.toThrow("must be one of:");
  });

  it("returns translated text on success", async () => {
    const apiResponse = JSON.stringify([{translations: [{text: "hola"}]}]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (translateText as any)({
      auth: {uid: "u1"},
      data: {text: "hello", to: "es-ES"},
    });
    expect(result).toEqual({translatedText: "hola"});
  });

  it("returns auto-detected source language when provided by API", async () => {
    const apiResponse = JSON.stringify([{
      translations: [{text: "bonjour"}],
      detectedLanguage: {language: "en", score: 0.95},
    }]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));

    const result = await (translateText as any)({
      auth: {uid: "u1"},
      data: {text: "hello", to: "fr-FR"},
    });

    expect(result).toEqual({
      translatedText: "bonjour",
      detectedLanguage: {language: "en", score: 0.95},
    });
  });

  it("throws on API error", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("error", false, 500));
    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hi", to: "en-US"}})
    ).rejects.toThrow("temporarily unavailable");
  });

  it("maps 400 responses to invalid-argument", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("bad request", false, 400));

    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hi", to: "en-US"}})
    ).rejects.toThrow("request is invalid");
  });

  it("maps 401 responses to failed-precondition", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("unauthorized", false, 401));

    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hi", to: "en-US"}})
    ).rejects.toThrow("authentication failed");
  });

  it("maps unexpected non-5xx errors to internal", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("teapot", false, 418));

    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hi", to: "en-US"}})
    ).rejects.toThrow("Translation service unavailable");
  });

  it("maps fetch failures to unavailable", async () => {
    mockFetch.mockRejectedValueOnce(new Error("network down"));

    await expect(
      (translateText as any)({auth: {uid: "u1"}, data: {text: "hi", to: "en-US"}})
    ).rejects.toThrow("Unable to reach translation service");
  });

  it("defaults detectedLanguage fields when API omits them", async () => {
    const apiResponse = JSON.stringify([{
      translations: [{text: "bonjour"}],
      detectedLanguage: {},
    }]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));

    const result = await (translateText as any)({
      auth: {uid: "u1"},
      data: {text: "hello", to: "fr-FR"},
    });

    expect(result).toEqual({
      translatedText: "bonjour",
      detectedLanguage: {language: "", score: 0},
    });
  });
});

// ── translateTexts ────────────────────────────────────────────────────

describe("translateTexts", () => {
  it("rejects batch exceeding max count", async () => {
    const texts = Array(801).fill("hi");
    await expect(
      (translateTexts as any)({auth: {uid: "u1"}, data: {texts, to: "en-US"}})
    ).rejects.toThrow("Too many texts");
  });

  it("rejects invalid target language code in batch", async () => {
    const texts = ["hello", "world"];
    await expect(
      (translateTexts as any)({auth: {uid: "u1"}, data: {texts, to: "invalid-code"}})
    ).rejects.toThrow("must be one of:");
  });

  it("rejects invalid source language code in batch", async () => {
    const texts = ["hello", "world"];
    await expect(
      (translateTexts as any)({auth: {uid: "u1"}, data: {texts, to: "en-US", from: "bad-lang"}})
    ).rejects.toThrow("must be one of:");
  });

  it("returns translated texts on success", async () => {
    const apiResponse = JSON.stringify([
      {translations: [{text: "hola"}]},
      {translations: [{text: "mundo"}]},
    ]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (translateTexts as any)({
      auth: {uid: "u1"},
      data: {texts: ["hello", "world"], to: "es-ES"},
    });
    expect(result).toEqual({translatedTexts: ["hola", "mundo"]});
  });

  it("accepts legacy short source language code for backward compatibility", async () => {
    const apiResponse = JSON.stringify([{translations: [{text: "你好"}]}]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));

    const result = await (translateTexts as any)({
      auth: {uid: "u1"},
      data: {texts: ["hello"], to: "zh-HK", from: "en"},
    });

    expect(result).toEqual({translatedTexts: ["你好"]});
  });

  it("handles empty texts array", async () => {
    const apiResponse = JSON.stringify([]);
    mockFetch.mockResolvedValueOnce(mockResponse(apiResponse));
    const result = await (translateTexts as any)({
      auth: {uid: "u1"},
      data: {texts: [], to: "en-US"},
    });
    expect(result).toEqual({translatedTexts: []});
  });

  it("throws on batch translation API error", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("batch error", false, 500));
    await expect(
      (translateTexts as any)({auth: {uid: "u1"}, data: {texts: ["a"], to: "en-US"}})
    ).rejects.toThrow("temporarily unavailable");
  });

  it("maps batch translation 429 responses to rate-limit error", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("rate limit", false, 429));

    await expect(
      (translateTexts as any)({auth: {uid: "u1"}, data: {texts: ["a"], to: "en-US"}})
    ).rejects.toThrow("rate limit exceeded");
  });

  it("maps batch fetch failures to unavailable", async () => {
    mockFetch.mockRejectedValueOnce(new Error("socket hang up"));

    await expect(
      (translateTexts as any)({auth: {uid: "u1"}, data: {texts: ["hello"], to: "zh-HK"}})
    ).rejects.toThrow("Unable to reach translation service");
  });

  it("returns empty list when batch payload is not an array", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse(JSON.stringify({translations: []})));

    const result = await (translateTexts as any)({
      auth: {uid: "u1"},
      data: {texts: ["hello"], to: "es-ES"},
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

  it("throws on language detection API error", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("detect error", false, 503));
    await expect(
      (detectLanguage as any)({auth: {uid: "u1"}, data: {text: "hello"}})
    ).rejects.toThrow("Language detection service is temporarily unavailable");
  });

  it("maps 400 detection responses to invalid-argument", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse("bad detect request", false, 400));

    await expect(
      (detectLanguage as any)({auth: {uid: "u1"}, data: {text: "hello"}})
    ).rejects.toThrow("Language detection service request is invalid");
  });

  it("maps detection fetch failures to unavailable", async () => {
    mockFetch.mockRejectedValueOnce(new Error("timeout"));

    await expect(
      (detectLanguage as any)({auth: {uid: "u1"}, data: {text: "hello"}})
    ).rejects.toThrow("Unable to reach language detection service");
  });

  it("returns default payload when detection JSON shape is not an array", async () => {
    mockFetch.mockResolvedValueOnce(mockResponse(JSON.stringify({language: "en"})));

    const result = await (detectLanguage as any)({
      auth: {uid: "u1"},
      data: {text: "hello"},
    });

    expect(result).toEqual({
      language: "",
      score: 0,
      isTranslationSupported: false,
      alternatives: [],
    });
  });
});
