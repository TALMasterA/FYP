/**
 * Unit tests for enforceRateLimit fail-closed behavior.
 */
import {HttpsError} from "firebase-functions/v2/https";

const mockGet = jest.fn();
const mockSet = jest.fn();
const mockRunTransaction = jest.fn();
const mockTx = {
  get: mockGet,
  set: mockSet,
};
const mockDoc = {
  get: jest.fn(),
  set: jest.fn(),
};

jest.mock("firebase-admin", () => ({
  firestore: Object.assign(
    jest.fn(() => ({
      runTransaction: mockRunTransaction,
      collection: jest.fn(() => ({
        doc: jest.fn(() => mockDoc),
      })),
    })),
    {
      FieldValue: {
        serverTimestamp: jest.fn(() => "mock-timestamp"),
      },
    }
  ),
  initializeApp: jest.fn(),
}));

jest.mock("firebase-functions/params", () => ({
  defineSecret: jest.fn((name: string) => ({name, value: () => "mock"})),
}));

import {
  enforceRateLimit,
  RATE_LIMIT_MAX_REQUESTS,
} from "../helpers.js";

describe("enforceRateLimit", () => {
  const uid = "user-1";

  beforeEach(() => {
    jest.clearAllMocks();
    mockRunTransaction.mockImplementation(async (callback: any) => callback(mockTx));
    mockSet.mockImplementation(() => undefined);
  });

  it("throws internal when Firestore transaction fails", async () => {
    mockRunTransaction.mockRejectedValueOnce(new Error("tx failed"));

    await expect(enforceRateLimit(uid)).rejects.toThrow(HttpsError);
  });

  it("throws internal when stored timestamps payload is malformed", async () => {
    mockGet.mockResolvedValueOnce({
      exists: true,
      data: () => ({timestamps: [Date.now(), "bad-value"]}),
    });

    await expect(enforceRateLimit(uid)).rejects.toThrow(
      "Rate limit data is invalid. Please try again."
    );
    expect(mockSet).not.toHaveBeenCalled();
  });

  it("keeps blocking when over limit", async () => {
    const now = Date.now();
    const timestamps = Array.from({length: RATE_LIMIT_MAX_REQUESTS}, () => now);

    mockGet.mockResolvedValueOnce({
      exists: true,
      data: () => ({timestamps}),
    });
    await expect(enforceRateLimit(uid)).rejects.toThrow(
      `Rate limit exceeded. Maximum ${RATE_LIMIT_MAX_REQUESTS} AI generation requests per hour.`
    );
    expect(mockSet).toHaveBeenCalledTimes(1);
  });

  it("throws internal when transaction body write fails", async () => {
    const now = Date.now();

    mockGet.mockResolvedValueOnce({
      exists: true,
      data: () => ({timestamps: [now]}),
    });
    mockSet.mockImplementationOnce(() => {
      throw new Error("write failed");
    });

    await expect(enforceRateLimit(uid)).rejects.toThrow(
      "Rate limit check failed. Please try again."
    );
  });

  it("allows request when under limit and write succeeds", async () => {
    const now = Date.now();

    mockGet.mockResolvedValueOnce({
      exists: true,
      data: () => ({timestamps: [now]}),
    });
    mockSet.mockResolvedValueOnce(undefined);

    await expect(enforceRateLimit(uid)).resolves.toBeUndefined();
    expect(mockSet).toHaveBeenCalledTimes(1);

    const setPayload = mockSet.mock.calls[0][1];
    expect(setPayload.timestamps).toHaveLength(2);
    expect(setPayload.updatedAt).toBe("mock-timestamp");
  });
});
