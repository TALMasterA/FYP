/**
 * Unit tests for helpers.ts — validation helpers, URL building, and language code mapping.
 */
import {HttpsError} from "firebase-functions/v2/https";

// Mock firebase-admin before importing helpers
jest.mock("firebase-admin", () => ({
  firestore: jest.fn(() => ({})),
  initializeApp: jest.fn(),
}));

// Mock firebase-functions/params defineSecret
jest.mock("firebase-functions/params", () => ({
  defineSecret: jest.fn((name: string) => ({name, value: () => "mock"})),
}));

import {
  getFirestore,
  requireAuth,
  requireString,
  safeParseJson,
  toTranslatorCode,
  buildTranslateUrl,
  validateLanguageCode,
  validateGenAiConfig,
} from "../helpers.js";

// ── getFirestore ─────────────────────────────────────────────────────

describe("getFirestore", () => {
  it("initializes firestore lazily once and reuses the cached instance", () => {
    const admin = jest.requireMock("firebase-admin");

    const first = getFirestore();
    const second = getFirestore();

    expect(first).toBe(second);
    expect(admin.firestore).toHaveBeenCalledTimes(1);
  });
});

// ── requireAuth ──────────────────────────────────────────────────────

describe("requireAuth", () => {
  it("throws unauthenticated when auth is null", () => {
    expect(() => requireAuth(null)).toThrow(HttpsError);
    expect(() => requireAuth(null)).toThrow("Login required.");
  });

  it("throws unauthenticated when auth is undefined", () => {
    expect(() => requireAuth(undefined)).toThrow(HttpsError);
  });

  it("does not throw when auth is present", () => {
    expect(() => requireAuth({uid: "user123"})).not.toThrow();
  });
});

// ── requireString ────────────────────────────────────────────────────

describe("requireString", () => {
  it("returns trimmed value for valid strings", () => {
    expect(requireString("  hello  ", "field")).toBe("hello");
  });

  it("throws invalid-argument for empty string", () => {
    expect(() => requireString("", "myField")).toThrow(HttpsError);
    expect(() => requireString("", "myField")).toThrow("myField is required");
  });

  it("throws invalid-argument for whitespace-only string", () => {
    expect(() => requireString("   ", "myField")).toThrow("myField is required");
  });

  it("throws invalid-argument for null", () => {
    expect(() => requireString(null, "myField")).toThrow("myField is required");
  });

  it("throws invalid-argument for undefined", () => {
    expect(() => requireString(undefined, "myField")).toThrow("myField is required");
  });

  it("coerces numbers to strings", () => {
    expect(requireString(42, "field")).toBe("42");
  });
});

// ── safeParseJson ────────────────────────────────────────────────────

describe("safeParseJson", () => {
  it("parses valid JSON", () => {
    const result = safeParseJson("{\"key\":\"value\"}", "test");
    expect(result).toEqual({key: "value"});
  });

  it("parses JSON arrays", () => {
    const result = safeParseJson("[1,2,3]", "test");
    expect(result).toEqual([1, 2, 3]);
  });

  it("throws HttpsError for invalid JSON", () => {
    expect(() => safeParseJson("not json", "test")).toThrow(HttpsError);
    expect(() => safeParseJson("not json", "test")).toThrow(
      "Invalid response from test service"
    );
  });
});

// ── toTranslatorCode ─────────────────────────────────────────────────

describe("toTranslatorCode", () => {
  it("maps zh-HK to yue", () => {
    expect(toTranslatorCode("zh-HK")).toBe("yue");
  });

  it("maps zh-TW to zh-Hant", () => {
    expect(toTranslatorCode("zh-TW")).toBe("zh-Hant");
  });

  it("maps zh-CN to zh-Hans", () => {
    expect(toTranslatorCode("zh-CN")).toBe("zh-Hans");
  });

  it("returns unmapped codes as-is", () => {
    expect(toTranslatorCode("en")).toBe("en");
    expect(toTranslatorCode("ja")).toBe("ja");
    expect(toTranslatorCode("ko")).toBe("ko");
  });
});

// ── buildTranslateUrl ────────────────────────────────────────────────

describe("buildTranslateUrl", () => {
  it("builds URL with to parameter", () => {
    const url = buildTranslateUrl({to: "en"});
    expect(url).toContain("/translate");
    expect(url).toContain("api-version=3.0");
    expect(url).toContain("to=en");
  });

  it("includes from parameter when provided", () => {
    const url = buildTranslateUrl({to: "en", from: "ja"});
    expect(url).toContain("from=ja");
  });

  it("omits from parameter when not provided", () => {
    const url = buildTranslateUrl({to: "en"});
    expect(url).not.toContain("from=");
  });

  it("normalises Chinese language codes in to and from", () => {
    const url = buildTranslateUrl({to: "zh-HK", from: "zh-CN"});
    expect(url).toContain("to=yue");
    expect(url).toContain("from=zh-Hans");
  });
});

// ── validateLanguageCode ────────────────────────────────────────────

describe("validateLanguageCode", () => {
  it("normalizes legacy short code alias en to en-US", () => {
    expect(validateLanguageCode("en", "from")).toBe("en-US");
  });

  it("accepts case-insensitive BCP-47 values and returns canonical casing", () => {
    expect(validateLanguageCode("EN-us", "to")).toBe("en-US");
  });
});

// ── validateGenAiConfig ─────────────────────────────────────────────

describe("validateGenAiConfig", () => {
  const validConfig = {
    baseUrl: "https://example.openai.azure.com",
    apiVersion: "2024-02-15-preview",
    apiKey: "test-key-12345",
  };

  it("returns trimmed config for valid values", () => {
    const result = validateGenAiConfig({
      baseUrl: `  ${validConfig.baseUrl}  `,
      apiVersion: `  ${validConfig.apiVersion}  `,
      apiKey: `  ${validConfig.apiKey}  `,
    });

    expect(result).toEqual(validConfig);
  });

  it("throws failed-precondition when any config value is missing", () => {
    expect(() =>
      validateGenAiConfig({...validConfig, baseUrl: ""})
    ).toThrow("AI service is not configured");

    expect(() =>
      validateGenAiConfig({...validConfig, apiVersion: ""})
    ).toThrow("AI service is not configured");

    expect(() =>
      validateGenAiConfig({...validConfig, apiKey: ""})
    ).toThrow("AI service is not configured");
  });

  it("throws failed-precondition for invalid base URL", () => {
    expect(() =>
      validateGenAiConfig({...validConfig, baseUrl: "not-a-url"})
    ).toThrow("AI service URL is misconfigured");
  });

  it("throws failed-precondition for non-https URL", () => {
    expect(() =>
      validateGenAiConfig({...validConfig, baseUrl: "http://example.com"})
    ).toThrow("AI service URL must use HTTPS");
  });

  it("throws failed-precondition for invalid API version", () => {
    expect(() =>
      validateGenAiConfig({...validConfig, apiVersion: "v1"})
    ).toThrow("AI service API version is misconfigured");
  });
});
