/**
 * Unit tests for log-redaction helpers (logUid, logChat) introduced in §5.3.1.
 *
 * The helpers are environment-sensitive: when LOG_SALT is unset they pass the
 * value through (local dev), and when set they emit a salted prefix-hashed
 * token so logs can be correlated without exposing the raw identifier.
 */
import {HttpsError} from "firebase-functions/v2/https";

jest.mock("firebase-admin", () => ({
  firestore: jest.fn(() => ({})),
  initializeApp: jest.fn(),
}));

jest.mock("firebase-functions/params", () => ({
  defineSecret: jest.fn((name: string) => ({name, value: () => "mock"})),
}));

// HttpsError is unused but keeps tsc happy alongside the firebase mocks.
void HttpsError;

describe("log redaction helpers", () => {
  const ORIGINAL_SALT = process.env.LOG_SALT;

  afterEach(() => {
    if (ORIGINAL_SALT === undefined) {
      delete process.env.LOG_SALT;
    } else {
      process.env.LOG_SALT = ORIGINAL_SALT;
    }
    jest.resetModules();
  });

  test("returns raw value when LOG_SALT is unset (dev mode)", async () => {
    delete process.env.LOG_SALT;
    jest.resetModules();
    const {logUid, logChat} = await import("../helpers.js");
    expect(logUid("alice-123")).toBe("alice-123");
    expect(logChat("chat-abc")).toBe("chat-abc");
  });

  test("hashes deterministically when LOG_SALT is set", async () => {
    process.env.LOG_SALT = "test-salt-1";
    jest.resetModules();
    const {logUid, logChat} = await import("../helpers.js");
    const a = logUid("alice");
    const b = logUid("alice");
    expect(a).toBe(b);
    expect(a).toMatch(/^u_[0-9a-f]{10}$/);
    const c = logChat("chat-1");
    expect(c).toMatch(/^c_[0-9a-f]{10}$/);
  });

  test("different salts produce different tokens for the same input", async () => {
    process.env.LOG_SALT = "salt-A";
    jest.resetModules();
    const modA = await import("../helpers.js");
    const tokenA = modA.logUid("alice");

    process.env.LOG_SALT = "salt-B";
    jest.resetModules();
    const modB = await import("../helpers.js");
    const tokenB = modB.logUid("alice");

    expect(tokenA).not.toBe(tokenB);
  });

  test("uid and chat namespaces are disjoint for identical input", async () => {
    process.env.LOG_SALT = "test-salt-1";
    jest.resetModules();
    const {logUid, logChat} = await import("../helpers.js");
    expect(logUid("same-id")).not.toBe(logChat("same-id"));
  });

  test("empty string returns sentinel", async () => {
    process.env.LOG_SALT = "test-salt-1";
    jest.resetModules();
    const {logUid, logChat} = await import("../helpers.js");
    expect(logUid("")).toBe("<empty>");
    expect(logChat("")).toBe("<empty>");
  });
});
