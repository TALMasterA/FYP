/**
 * Unit tests for accountDeletion.ts.
 */
import {beforeEach, describe, expect, it, jest} from "@jest/globals";

const mockDeleteUser = jest.fn(async (uid: string) => {
  void uid;
  return undefined;
});
const mockGetUser = jest.fn(async (uid: string) => {
  void uid;
  return {email: "user@example.com"};
});
const mockBatchDelete = jest.fn();
const mockBatchCommit = jest.fn(async () => undefined);
const mockMailAdd = jest.fn(async (payload: any) => {
  void payload;
  return {id: "mail1"};
});
const mockUserDocDelete = jest.fn(async () => undefined);
const mockProfileDocDelete = jest.fn(async () => undefined);
const mockUserSearchDelete = jest.fn(async () => undefined);
const mockUsernameDelete = jest.fn(async () => undefined);
const collectionNames: string[] = [];

const emptySnapshot = {empty: true, size: 0, docs: []};

function collectionRef(name: string): any {
  collectionNames.push(name);
  return {
    limit: jest.fn(() => ({get: jest.fn(async () => emptySnapshot)})),
    doc: jest.fn((id: string) => {
      if (name === "users") return userDocRef(id);
      if (name === "user_search") return {delete: mockUserSearchDelete};
      if (name === "profile") return {delete: mockProfileDocDelete};
      return {delete: jest.fn()};
    }),
    where: jest.fn(() => ({
      limit: jest.fn(() => ({
        get: jest.fn(async () => ({
          empty: false,
          size: 1,
          docs: [{ref: {delete: mockUsernameDelete}}],
        })),
      })),
    })),
    add: mockMailAdd,
  };
}

function userDocRef(id: string): any {
  return {
    id,
    collection: jest.fn((name: string) => collectionRef(name)),
    delete: mockUserDocDelete,
  };
}

const mockFirestore = {
  collection: jest.fn((name: string) => collectionRef(name)),
  batch: jest.fn(() => ({
    delete: mockBatchDelete,
    commit: mockBatchCommit,
  })),
};

jest.mock("../functionWrappers.js", () => ({
  onAppCheckCall: jest.fn((_opts: any, handler: any) => handler),
}));

jest.mock("../helpers.js", () => ({
  getFirestore: jest.fn(() => mockFirestore),
  requireAuth: jest.fn((auth: any) => {
    if (!auth) throw new Error("auth required");
  }),
}));

jest.mock("firebase-admin", () => ({
  auth: jest.fn(() => ({
    getUser: mockGetUser,
    deleteUser: mockDeleteUser,
  })),
  firestore: {
    FieldValue: {
      serverTimestamp: jest.fn(() => "SERVER_TIMESTAMP"),
    },
    Timestamp: {
      fromDate: jest.fn((date: Date) => ({toDate: () => date})),
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

import {deleteAccountAndData} from "../accountDeletion.js";

describe("deleteAccountAndData", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    collectionNames.length = 0;
    delete process.env.ACCOUNT_DELETION_EMAIL_ENABLED;
    mockGetUser.mockResolvedValue({email: "user@example.com"});
    mockDeleteUser.mockResolvedValue(undefined);
    mockBatchCommit.mockResolvedValue(undefined);
    mockMailAdd.mockResolvedValue({id: "mail1"});
    mockUserDocDelete.mockResolvedValue(undefined);
    mockProfileDocDelete.mockResolvedValue(undefined);
    mockUserSearchDelete.mockResolvedValue(undefined);
    mockUsernameDelete.mockResolvedValue(undefined);
  });

  it("deletes every documented user subcollection before deleting Auth user", async () => {
    const result = await (deleteAccountAndData as any)({auth: {uid: "user1"}, data: {}});

    expect(result).toEqual({success: true, confirmationEmailQueued: false});
    expect(collectionNames).toEqual(expect.arrayContaining([
      "history",
      "word_banks",
      "learning_sheets",
      "quiz_attempts",
      "quiz_stats",
      "generated_quizzes",
      "quiz_versions",
      "favorites",
      "custom_words",
      "sessions",
      "coin_awards",
      "last_awarded_quiz",
      "user_stats",
      "friends",
      "shared_inbox",
      "favorite_sessions",
      "blocked_users",
      "fcm_tokens",
    ]));
    expect(mockProfileDocDelete).toHaveBeenCalledTimes(3);
    expect(mockUsernameDelete).toHaveBeenCalledTimes(1);
    expect(mockUserSearchDelete).toHaveBeenCalledTimes(1);
    expect(mockUserDocDelete).toHaveBeenCalledTimes(1);
    expect(mockDeleteUser).toHaveBeenCalledWith("user1");
  });

  it("queues confirmation email only when explicitly enabled", async () => {
    process.env.ACCOUNT_DELETION_EMAIL_ENABLED = "true";

    const result = await (deleteAccountAndData as any)({auth: {uid: "user1"}, data: {}});

    expect(result).toEqual({success: true, confirmationEmailQueued: true});
    expect(mockMailAdd).toHaveBeenCalledWith(expect.objectContaining({
      to: ["user@example.com"],
      type: "account_deletion_confirmation",
    }));
  });
});
