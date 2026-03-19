/**
 * Unit tests for health.ts readiness endpoint.
 */

const mockValidateGenAiConfig = jest.fn();
const mockLoggerError = jest.fn();

jest.mock("firebase-functions/v2/https", () => {
  const actual = jest.requireActual("firebase-functions/v2/https");
  return {
    ...actual,
    onRequest: jest.fn((opts: any, handler?: any) => handler || opts),
  };
});

jest.mock("../helpers.js", () => ({
  GENAI_BASE_URL: {value: jest.fn(() => "https://example.openai.azure.com")},
  GENAI_API_VERSION: {value: jest.fn(() => "2024-02-15-preview")},
  GENAI_API_KEY: {value: jest.fn(() => "test-key")},
  validateGenAiConfig: (...args: any[]) => mockValidateGenAiConfig(...args),
}));

jest.mock("../logger.js", () => ({
  logger: {
    error: (...args: any[]) => mockLoggerError(...args),
  },
}));

import {healthcheck} from "../health.js";

describe("healthcheck", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockValidateGenAiConfig.mockReturnValue({
      baseUrl: "https://example.openai.azure.com",
      apiVersion: "2024-02-15-preview",
      apiKey: "test-key",
    });
  });

  function createResponse() {
    return {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
    };
  }

  it("returns 200 when configuration is valid", async () => {
    const req = {method: "GET"};
    const res = createResponse();

    await healthcheck(req as any, res as any);

    expect(mockValidateGenAiConfig).toHaveBeenCalledTimes(1);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith(
      expect.objectContaining({
        status: "ok",
        checks: {genAiConfig: "ok"},
      })
    );
  });

  it("returns 500 when configuration validation fails", async () => {
    mockValidateGenAiConfig.mockImplementation(() => {
      throw new Error("invalid config");
    });

    const req = {method: "GET"};
    const res = createResponse();

    await healthcheck(req as any, res as any);

    expect(mockLoggerError).toHaveBeenCalledTimes(1);
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith(
      expect.objectContaining({
        status: "error",
        checks: {genAiConfig: "error"},
      })
    );
  });

  it("returns 405 for unsupported methods", async () => {
    const req = {method: "POST"};
    const res = createResponse();

    await healthcheck(req as any, res as any);

    expect(mockValidateGenAiConfig).not.toHaveBeenCalled();
    expect(res.status).toHaveBeenCalledWith(405);
    expect(res.json).toHaveBeenCalledWith(
      expect.objectContaining({
        status: "error",
        message: "Method not allowed",
      })
    );
  });
});
