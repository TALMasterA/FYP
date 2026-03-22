/**
 * Unit tests for maintenance.ts — scheduled cleanup tasks.
 *
 * These tests verify the core logic of maintenance functions
 * by mocking Firestore operations.
 */

// ── Mock setup ──────────────────────────────────────────────────────────

const mockBatchDelete = jest.fn();
const mockBatchSet = jest.fn();
const mockBatchCommit = jest.fn().mockResolvedValue(undefined);

const mockGet = jest.fn();

// Create chainable mock for Firestore queries
const createQueryMock = (): any => ({
  get: mockGet,
  where: jest.fn().mockReturnThis(),
  limit: jest.fn().mockReturnThis(),
  select: jest.fn().mockReturnThis(),
  startAfter: jest.fn().mockReturnThis(),
});

const mockFirestore = {
  collection: jest.fn(() => createQueryMock()),
  batch: jest.fn(() => ({
    delete: mockBatchDelete,
    set: mockBatchSet,
    commit: mockBatchCommit,
  })),
};

jest.mock("../helpers.js", () => ({
  getFirestore: jest.fn(() => mockFirestore),
}));

jest.mock("firebase-admin", () => ({
  firestore: {
    Timestamp: {
      fromDate: jest.fn((date: Date) => ({
        toDate: () => date,
        _seconds: Math.floor(date.getTime() / 1000),
      })),
    },
    FieldValue: {
      delete: jest.fn(() => "DELETE_SENTINEL"),
    },
  },
  initializeApp: jest.fn(),
}));

jest.mock("../logger.js", () => ({
  logger: {
    info: jest.fn(),
    error: jest.fn(),
    warn: jest.fn(),
  },
}));

// Mock onSchedule to return the handler directly
jest.mock("firebase-functions/v2/scheduler", () => ({
  onSchedule: jest.fn((opts: any, handler: any) => handler),
}));

// ── Import after mocks ────────────────────────────────────────────────

import {pruneStaleTokens, pruneStaleRateLimits, repairFriendsData} from "../maintenance.js";
import {logger} from "../logger.js";

// ── Helper to create mock snapshots ───────────────────────────────────

function createMockSnapshot(docs: Array<{id: string; data?: Record<string, any>}>) {
  return {
    empty: docs.length === 0,
    size: docs.length,
    docs: docs.map((d) => ({
      id: d.id,
      data: () => d.data || {},
      ref: {id: d.id},
    })),
  };
}

// ── Tests ─────────────────────────────────────────────────────────────

describe("pruneStaleTokens", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("handles empty users collection", async () => {
    mockGet.mockResolvedValueOnce(createMockSnapshot([]));

    await (pruneStaleTokens as any)();

    expect(logger.info).toHaveBeenCalledWith("pruneStaleTokens: removed 0 stale tokens");
  });

  it("queries users collection with pagination", async () => {
    // Return empty collection immediately to test basic path
    mockGet.mockResolvedValue(createMockSnapshot([]));

    await (pruneStaleTokens as any)();

    expect(mockFirestore.collection).toHaveBeenCalledWith("users");
    expect(logger.info).toHaveBeenCalled();
  });
});

describe("pruneStaleRateLimits", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("handles no stale rate limits", async () => {
    mockGet.mockResolvedValueOnce(createMockSnapshot([]));

    await (pruneStaleRateLimits as any)();

    expect(logger.info).toHaveBeenCalledWith("pruneStaleRateLimits: nothing to prune");
    expect(mockBatchCommit).not.toHaveBeenCalled();
  });

  it("queries rate_limits collection", async () => {
    mockGet.mockResolvedValue(createMockSnapshot([]));

    await (pruneStaleRateLimits as any)();

    expect(mockFirestore.collection).toHaveBeenCalledWith("rate_limits");
  });

  it("deletes stale rate limit documents in batches", async () => {
    mockGet
      .mockResolvedValueOnce(createMockSnapshot([
        {id: "user1"},
        {id: "user2"},
      ]))
      .mockResolvedValueOnce(createMockSnapshot([]));

    await (pruneStaleRateLimits as any)();

    expect(mockBatchDelete).toHaveBeenCalledTimes(2);
    expect(mockBatchCommit).toHaveBeenCalledTimes(1);
    expect(logger.info).toHaveBeenCalledWith("pruneStaleRateLimits: removed 2 stale rate-limit docs");
  });
});

describe("repairFriendsData", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("handles no cancelled requests and no malformed docs", async () => {
    // Return empty for all queries
    mockGet.mockResolvedValue(createMockSnapshot([]));

    await (repairFriendsData as any)();

    expect(logger.info).toHaveBeenCalledWith("repairFriendsData completed", {
      cancelledDeleted: 0,
      expiredDeleted: 0,
      repairedUserSearchDocs: 0,
      forcedPrivateProfiles: 0,
    });
  });

  it("queries friend_requests collection for CANCELLED status", async () => {
    mockGet.mockResolvedValue(createMockSnapshot([]));

    await (repairFriendsData as any)();

    expect(mockFirestore.collection).toHaveBeenCalledWith("friend_requests");
  });

  it("queries user_search collection for malformed docs", async () => {
    mockGet.mockResolvedValue(createMockSnapshot([]));

    await (repairFriendsData as any)();

    expect(mockFirestore.collection).toHaveBeenCalledWith("user_search");
  });

  it("deletes cancelled friend requests", async () => {
    mockGet
      // First batch of cancelled requests
      .mockResolvedValueOnce(createMockSnapshot([
        {id: "req1"},
        {id: "req2"},
      ]))
      // No more cancelled
      .mockResolvedValueOnce(createMockSnapshot([]))
      // No expired PENDING requests
      .mockResolvedValueOnce(createMockSnapshot([]))
      // No user_search docs
      .mockResolvedValueOnce(createMockSnapshot([]));

    await (repairFriendsData as any)();

    expect(mockBatchDelete).toHaveBeenCalledTimes(2);
    expect(logger.info).toHaveBeenCalledWith("repairFriendsData completed", {
      cancelledDeleted: 2,
      expiredDeleted: 0,
      repairedUserSearchDocs: 0,
      forcedPrivateProfiles: 0,
    });
  });
});
