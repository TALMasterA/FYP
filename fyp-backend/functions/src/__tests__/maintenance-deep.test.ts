/**
 * Deep branch tests for maintenance.ts scheduled jobs.
 */

const mockBatchDelete = jest.fn();
const mockBatchSet = jest.fn();
const mockBatchCommit = jest.fn().mockResolvedValue(undefined);

const queues = {
  usersPages: [] as any[],
  rateLimitPages: [] as any[],
  cancelledPages: [] as any[],
  userSearchPages: [] as any[],
};

const tokensByUser = new Map<string, any>();
const publicProfileByUser = new Map<string, any>();

function createSnap(
  docs: Array<{id: string; data?: Record<string, unknown>; ref?: any}>,
  sizeOverride?: number
) {
  return {
    empty: docs.length === 0,
    size: sizeOverride ?? docs.length,
    docs: docs.map((d) => ({
      id: d.id,
      data: () => d.data ?? {},
      ref: d.ref ?? {path: `ref/${d.id}`},
    })),
  };
}

function shiftOrEmpty(name: keyof typeof queues) {
  const arr = queues[name];
  if (arr.length === 0) return createSnap([]);
  return arr.shift();
}

function usersCollection(): any {
  const query: any = {
    select: jest.fn(() => query),
    limit: jest.fn(() => query),
    startAfter: jest.fn(() => query),
    get: jest.fn(async () => shiftOrEmpty("usersPages")),
  };

  query.doc = jest.fn((userId: string) => ({
    collection: jest.fn((sub: string) => {
      if (sub === "fcm_tokens") {
        return {
          where: jest.fn(() => ({
            get: jest.fn(async () => tokensByUser.get(userId) ?? createSnap([])),
          })),
        };
      }

      if (sub === "profile") {
        return {
          doc: jest.fn(() => ({
            get: jest.fn(async () => publicProfileByUser.get(userId) ?? {exists: false, data: () => ({})}),
            path: `users/${userId}/profile/public`,
          })),
        };
      }

      throw new Error(`Unexpected users subcollection: ${sub}`);
    }),
  }));

  return query;
}

function rateLimitsCollection(): any {
  const query: any = {
    where: jest.fn(() => query),
    limit: jest.fn(() => query),
    startAfter: jest.fn(() => query),
    get: jest.fn(async () => shiftOrEmpty("rateLimitPages")),
  };
  return query;
}

function friendRequestsCollection(): any {
  const query: any = {
    where: jest.fn(() => query),
    limit: jest.fn(() => query),
    get: jest.fn(async () => shiftOrEmpty("cancelledPages")),
  };
  return query;
}

function userSearchCollection(): any {
  const query: any = {
    limit: jest.fn(() => query),
    startAfter: jest.fn(() => query),
    get: jest.fn(async () => shiftOrEmpty("userSearchPages")),
  };
  return query;
}

const mockFirestore = {
  collection: jest.fn((name: string) => {
    if (name === "users") return usersCollection();
    if (name === "rate_limits") return rateLimitsCollection();
    if (name === "friend_requests") return friendRequestsCollection();
    if (name === "user_search") return userSearchCollection();
    throw new Error(`Unexpected collection: ${name}`);
  }),
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
      fromDate: jest.fn((date: Date) => ({toDate: () => date})),
    },
    FieldValue: {
      delete: jest.fn(() => "DELETE_SENTINEL"),
    },
  },
}));

jest.mock("../logger.js", () => ({
  logger: {
    info: jest.fn(),
    warn: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock("firebase-functions/v2/scheduler", () => ({
  onSchedule: jest.fn((opts: any, handler: any) => handler),
}));

import {pruneStaleTokens, pruneStaleRateLimits, repairFriendsData} from "../maintenance.js";
import {logger} from "../logger.js";

describe("maintenance deep branches", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    queues.usersPages = [];
    queues.rateLimitPages = [];
    queues.cancelledPages = [];
    queues.userSearchPages = [];
    tokensByUser.clear();
    publicProfileByUser.clear();
  });

  it("paginates token pruning and skips users without stale tokens", async () => {
    queues.usersPages.push(
      createSnap([{id: "u1"}, {id: "u2"}], 500),
      createSnap([{id: "u3"}], 1)
    );

    tokensByUser.set("u1", createSnap([
      {id: "t1", ref: {path: "users/u1/fcm_tokens/t1"}},
      {id: "t2", ref: {path: "users/u1/fcm_tokens/t2"}},
    ], 2));
    tokensByUser.set("u2", createSnap([], 0));
    tokensByUser.set("u3", createSnap([
      {id: "t3", ref: {path: "users/u3/fcm_tokens/t3"}},
    ], 1));

    await (pruneStaleTokens as any)();

    expect(mockBatchDelete).toHaveBeenCalledTimes(3);
    expect(mockBatchCommit).toHaveBeenCalledTimes(2);
    expect(logger.info).toHaveBeenCalledWith("pruneStaleTokens: removed 3 stale tokens");
  });

  it("paginates stale rate-limits with startAfter branch", async () => {
    queues.rateLimitPages.push(
      createSnap([
        {id: "r1", ref: {path: "rate_limits/r1"}},
        {id: "r2", ref: {path: "rate_limits/r2"}},
      ], 500),
      createSnap([{id: "r3", ref: {path: "rate_limits/r3"}}], 1)
    );

    await (pruneStaleRateLimits as any)();

    expect(mockBatchDelete).toHaveBeenCalledTimes(3);
    expect(mockBatchCommit).toHaveBeenCalledTimes(2);
    expect(logger.info).toHaveBeenCalledWith("pruneStaleRateLimits: removed 501 stale rate-limit docs");
  });

  it("repairs mixed legacy user_search drift and profile flags", async () => {
    queues.cancelledPages.push(
      createSnap([
        {id: "c1", ref: {path: "friend_requests/c1"}},
        {id: "c2", ref: {path: "friend_requests/c2"}},
      ], 300),
      createSnap([{id: "c3", ref: {path: "friend_requests/c3"}}], 1)
    );

    queues.userSearchPages.push(
      createSnap([
        {
          id: "a",
          data: {
            username: "Alice",
            username_lowercase: "ALICE",
            isDiscoverable: true,
          },
          ref: {path: "user_search/a"},
        },
        {
          id: "b",
          data: {
            username: " ",
            username_lowercase: "legacy",
            isDiscoverable: true,
          },
          ref: {path: "user_search/b"},
        },
        {
          id: "c",
          data: {
            username: "",
            username_lowercase: "",
            isDiscoverable: false,
          },
          ref: {path: "user_search/c"},
        },
      ], 300),
      createSnap([
        {
          id: "d",
          data: {
            username: "",
            username_lowercase: "old",
            isDiscoverable: false,
          },
          ref: {path: "user_search/d"},
        },
      ], 1)
    );

    publicProfileByUser.set("b", {
      exists: true,
      data: () => ({
        username: "",
        isDiscoverable: true,
        discoverable: true,
      }),
    });

    publicProfileByUser.set("c", {
      exists: true,
      data: () => ({
        username: "Bob",
        isDiscoverable: true,
        discoverable: true,
      }),
    });

    publicProfileByUser.set("d", {
      exists: false,
      data: () => ({}),
    });

    await (repairFriendsData as any)();

    expect(mockBatchDelete).toHaveBeenCalledTimes(3);
    expect(mockBatchSet).toHaveBeenCalledWith(
      expect.objectContaining({path: "user_search/a"}),
      expect.objectContaining({username_lowercase: "alice"}),
      {merge: true}
    );
    expect(mockBatchSet).toHaveBeenCalledWith(
      expect.objectContaining({path: "users/b/profile/public"}),
      expect.objectContaining({isDiscoverable: false, discoverable: "DELETE_SENTINEL"}),
      {merge: true}
    );
    expect(mockBatchSet).toHaveBeenCalledWith(
      expect.objectContaining({path: "users/c/profile/public"}),
      expect.objectContaining({discoverable: "DELETE_SENTINEL"}),
      {merge: true}
    );
    expect(mockBatchCommit).toHaveBeenCalledTimes(4);

    expect(logger.info).toHaveBeenCalledWith("repairFriendsData completed", {
      cancelledDeleted: 301,
      repairedUserSearchDocs: 3,
      forcedPrivateProfiles: 2,
    });
  });
});
