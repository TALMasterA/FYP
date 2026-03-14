/**
 * Unit tests for logger.ts — structured logging utility.
 */
import {logger} from "../logger.js";

describe("logger", () => {
  let consoleSpy: {
    log: jest.SpyInstance;
    warn: jest.SpyInstance;
    error: jest.SpyInstance;
    debug: jest.SpyInstance;
  };

  beforeEach(() => {
    consoleSpy = {
      log: jest.spyOn(console, "log").mockImplementation(),
      warn: jest.spyOn(console, "warn").mockImplementation(),
      error: jest.spyOn(console, "error").mockImplementation(),
      debug: jest.spyOn(console, "debug").mockImplementation(),
    };
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it("logs INFO messages via console.log", () => {
    logger.info("test info");
    expect(consoleSpy.log).toHaveBeenCalledTimes(1);
    const payload = JSON.parse(consoleSpy.log.mock.calls[0][0]);
    expect(payload.severity).toBe("INFO");
    expect(payload.message).toBe("test info");
  });

  it("logs WARN messages via console.warn", () => {
    logger.warn("test warn");
    expect(consoleSpy.warn).toHaveBeenCalledTimes(1);
    const payload = JSON.parse(consoleSpy.warn.mock.calls[0][0]);
    expect(payload.severity).toBe("WARN");
    expect(payload.message).toBe("test warn");
  });

  it("logs ERROR messages via console.error", () => {
    logger.error("test error");
    expect(consoleSpy.error).toHaveBeenCalledTimes(1);
    const payload = JSON.parse(consoleSpy.error.mock.calls[0][0]);
    expect(payload.severity).toBe("ERROR");
    expect(payload.message).toBe("test error");
  });

  it("logs DEBUG messages via console.debug", () => {
    logger.debug("test debug");
    expect(consoleSpy.debug).toHaveBeenCalledTimes(1);
    const payload = JSON.parse(consoleSpy.debug.mock.calls[0][0]);
    expect(payload.severity).toBe("DEBUG");
    expect(payload.message).toBe("test debug");
  });

  it("includes metadata in log output", () => {
    logger.info("with meta", {userId: "abc", count: 42});
    const payload = JSON.parse(consoleSpy.log.mock.calls[0][0]);
    expect(payload.userId).toBe("abc");
    expect(payload.count).toBe(42);
  });

  it("emits valid JSON", () => {
    logger.error("json test", {special: 'chars "and" more'});
    expect(() => {
      JSON.parse(consoleSpy.error.mock.calls[0][0]);
    }).not.toThrow();
  });
});
