/**
 * Unit tests for learning.ts — AI-powered learning content generation.
 */

// ── Mock setup ──────────────────────────────────────────────────────────

// Create a proper HttpsError mock class we can use
class MockHttpsError extends Error {
  code: string;
  constructor(code: string, message: string) {
    super(message);
    this.code = code;
    this.name = "HttpsError";
  }
}

const mockFetch = jest.fn();
jest.mock("node-fetch", () => ({
  __esModule: true,
  default: mockFetch,
}));

// Mock rate limiting
const mockEnforceRateLimit = jest.fn();
const mockValidateGenAiConfig = jest.fn();

jest.mock("../helpers.js", () => ({
  requireAuth: jest.fn((auth: any) => {
    if (!auth) throw new MockHttpsError("unauthenticated", "Login required.");
    return auth;
  }),
  requireString: jest.fn((value: any, name: string) => {
    const str = String(value ?? "").trim();
    if (!str) {
      throw new MockHttpsError("invalid-argument", `${name} is required`);
    }
    return str;
  }),
  enforceRateLimit: mockEnforceRateLimit,
  validateGenAiConfig: mockValidateGenAiConfig,
  GENAI_BASE_URL: {value: () => "https://test.openai.azure.com"},
  GENAI_API_VERSION: {value: () => "2023-05-15"},
  GENAI_API_KEY: {value: () => "test-api-key"},
}));

jest.mock("../logger.js", () => ({
  logger: {
    info: jest.fn(),
    error: jest.fn(),
    warn: jest.fn(),
  },
}));

// Mock onCall to extract the handler
jest.mock("firebase-functions/v2/https", () => {
  return {
    HttpsError: MockHttpsError,
    onCall: jest.fn((opts: any, handler?: any) => {
      const fn = handler || opts;
      return fn;
    }),
  };
});

// ── Import after mocks ────────────────────────────────────────────────

import {generateLearningContent} from "../learning.js";

// ── Tests ─────────────────────────────────────────────────────────────

describe("generateLearningContent", () => {
  const validData = {
    deployment: "gpt-4o",
    prompt: "Generate a vocabulary exercise for English-Chinese translation",
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockEnforceRateLimit.mockResolvedValue(undefined);
    mockValidateGenAiConfig.mockReturnValue({
      baseUrl: "https://test.openai.azure.com",
      apiVersion: "2023-05-15",
      apiKey: "test-api-key",
    });
  });

  it("rejects unauthenticated requests", async () => {
    await expect(
      (generateLearningContent as any)({auth: null, data: validData})
    ).rejects.toThrow("Login required.");
  });

  it("rejects missing deployment", async () => {
    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: {...validData, deployment: ""}})
    ).rejects.toThrow("deployment is required");
  });

  it("rejects missing prompt", async () => {
    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: {...validData, prompt: ""}})
    ).rejects.toThrow("prompt is required");
  });

  it("enforces rate limiting before making API call", async () => {
    mockEnforceRateLimit.mockRejectedValue(
      new MockHttpsError("resource-exhausted", "Rate limit exceeded")
    );

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("Rate limit exceeded");

    expect(mockEnforceRateLimit).toHaveBeenCalledWith("u1");
    expect(mockFetch).not.toHaveBeenCalled();
  });

  it("handles GenAI config validation failure", async () => {
    mockValidateGenAiConfig.mockImplementation(() => {
      throw new MockHttpsError("failed-precondition", "Invalid configuration");
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("Invalid configuration");
  });

  it("handles network errors gracefully", async () => {
    mockFetch.mockRejectedValue(new Error("Network error"));

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("Unable to reach AI service");
  });

  it("handles 401 authentication errors", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 401,
      statusText: "Unauthorized",
      text: jest.fn().mockResolvedValue("Unauthorized"),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("AI service authentication failed");
  });

  it("handles 403 authorization errors", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 403,
      statusText: "Forbidden",
      text: jest.fn().mockResolvedValue("Forbidden"),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("AI service authentication failed");
  });

  it("handles 429 rate limit errors from Azure", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 429,
      statusText: "Too Many Requests",
      text: jest.fn().mockResolvedValue("Rate limit exceeded"),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("AI service rate limit exceeded");
  });

  it("handles 404 deployment not found errors", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 404,
      statusText: "Not Found",
      text: jest.fn().mockResolvedValue("Deployment not found"),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("AI model configuration not found");
  });

  it("handles 5xx server errors", async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 500,
      statusText: "Internal Server Error",
      text: jest.fn().mockResolvedValue("Server error"),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("AI service is temporarily unavailable");
  });

  it("handles invalid JSON response", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue("not valid json"),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("Invalid response from AI service");
  });

  it("handles empty content response", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue(JSON.stringify({choices: []})),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("No content generated");
  });

  it("handles missing message content", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue(JSON.stringify({
        choices: [{message: {}}],
      })),
    });

    await expect(
      (generateLearningContent as any)({auth: {uid: "u1"}, data: validData})
    ).rejects.toThrow("No content generated");
  });

  it("successfully generates learning content", async () => {
    const generatedContent = "Here is your vocabulary exercise...";
    mockFetch.mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue(JSON.stringify({
        choices: [{message: {content: generatedContent}}],
      })),
    });

    const result = await (generateLearningContent as any)({
      auth: {uid: "u1"},
      data: validData,
    });

    expect(result).toEqual({content: generatedContent});
    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("gpt-4o"),
      expect.objectContaining({
        method: "POST",
        headers: expect.objectContaining({
          "Content-Type": "application/json",
          "api-key": "test-api-key",
        }),
      })
    );
  });

  it("sends correct request body format", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue(JSON.stringify({
        choices: [{message: {content: "content"}}],
      })),
    });

    await (generateLearningContent as any)({auth: {uid: "u1"}, data: validData});

    const callArgs = mockFetch.mock.calls[0];
    const body = JSON.parse(callArgs[1].body);
    expect(body).toEqual({
      messages: [
        {role: "system", content: "You are a helpful language learning assistant."},
        {role: "user", content: validData.prompt},
      ],
      temperature: 1,
    });
  });

  it("constructs correct Azure OpenAI URL", async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue(JSON.stringify({
        choices: [{message: {content: "content"}}],
      })),
    });

    await (generateLearningContent as any)({auth: {uid: "u1"}, data: validData});

    const callArgs = mockFetch.mock.calls[0];
    const url = callArgs[0];
    expect(url).toContain("test.openai.azure.com");
    expect(url).toContain("/openai/deployments/gpt-4o/chat/completions");
    expect(url).toContain("api-version=2023-05-15");
  });
});
