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
  requireAuth,
  requireString,
  optionalString,
  safeParseJson,
  toTranslatorCode,
  buildTranslateUrl,
} from "../helpers.js";

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

// ── optionalString ───────────────────────────────────────────────────

describe("optionalString", () => {
  it("returns trimmed string for valid input", () => {
    expect(optionalString("  hi  ")).toBe("hi");
  });

  it("returns empty string for null", () => {
    expect(optionalString(null)).toBe("");
  });

  it("returns empty string for undefined", () => {
    expect(optionalString(undefined)).toBe("");
  });

  it("returns empty string for empty string", () => {
    expect(optionalString("")).toBe("");
  });
});

// ── safeParseJson ────────────────────────────────────────────────────

describe("safeParseJson", () => {
  it("parses valid JSON", () => {
    const result = safeParseJson('{"key":"value"}', "test");
    expect(result).toEqual({key: "value"});
  });

  it("parses JSON arrays", () => {
    const result = safeParseJson('[1,2,3]', "test");
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
