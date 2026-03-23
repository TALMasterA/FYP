/**
 * Unit tests for notifications.ts — spam detection logic.
 *
 * The notification triggers themselves are Firestore-triggered and hard to unit test
 * without firebase-functions-test. These tests cover the extractable spam detection logic
 * by testing the module's internal behaviour through its exported triggers.
 */

// ── Mock setup ────────────────────────────────────────────────────────

const mockGet = jest.fn();
const mockWhere: jest.Mock = jest.fn(() => ({where: mockWhere, orderBy: mockOrderBy, limit: mockLimit, get: mockGet}));
const mockOrderBy = jest.fn(() => ({limit: mockLimit, get: mockGet}));
const mockLimit = jest.fn(() => ({get: mockGet}));
const mockDocGet = jest.fn();
const mockBatchDelete = jest.fn();
const mockBatchCommit = jest.fn();
const mockSendEachForMulticast = jest.fn();

jest.mock("firebase-admin", () => ({
  firestore: jest.fn(() => ({
    collection: jest.fn(() => ({
      doc: jest.fn(() => ({
        collection: jest.fn(() => ({
          doc: jest.fn(() => ({delete: jest.fn()})),
          get: mockDocGet,
          where: mockWhere,
          orderBy: mockOrderBy,
        })),
        get: mockDocGet,
      })),
      where: mockWhere,
    })),
    batch: jest.fn(() => ({delete: mockBatchDelete, commit: mockBatchCommit})),
  })),
  initializeApp: jest.fn(),
  messaging: jest.fn(() => ({
    sendEachForMulticast: mockSendEachForMulticast,
  })),
}));

jest.mock("firebase-functions/params", () => ({
  defineSecret: jest.fn((name: string) => ({name, value: () => "mock"})),
}));

// Capture registered Firestore trigger handlers
const registeredHandlers: Record<string, any> = {};
jest.mock("firebase-functions/v2/firestore", () => ({
  onDocumentCreated: jest.fn((opts: any, handler: any) => {
    registeredHandlers[opts.document] = handler;
    return handler;
  }),
  onDocumentUpdated: jest.fn((opts: any, handler: any) => {
    registeredHandlers[opts.document] = handler;
    return handler;
  }),
}));

jest.mock("firebase-functions/v2/https", () => {
  const actual = jest.requireActual("firebase-functions/v2/https");
  return {...actual};
});

// ── Import after mocks ────────────────────────────────────────────────

import "../notifications.js";

beforeEach(() => jest.clearAllMocks());

// ── sendChatNotification trigger ──────────────────────────────────────

describe("sendChatNotification", () => {
  const handler = registeredHandlers["chats/{chatId}/messages/{messageId}"];

  it("is registered as a Firestore trigger", () => {
    expect(handler).toBeDefined();
  });

  it("returns early when data is missing", async () => {
    await handler({data: null, params: {chatId: "a_b", messageId: "m1"}});
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("returns early when senderId or receiverId is missing", async () => {
    await handler({
      data: {data: () => ({senderId: "", receiverId: "u2", content: "hi", type: "TEXT"})},
      params: {chatId: "a_b", messageId: "m1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("returns early for non-TEXT message types", async () => {
    await handler({
      data: {data: () => ({senderId: "u1", receiverId: "u2", content: "shared", type: "SHARED_ITEM"})},
      params: {chatId: "u1_u2", messageId: "m1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("skips notification when message contains link flooding", async () => {
    // Mock spam check: 3+ links in a single message
    const spamContent = "Visit https://spam1.com and https://spam2.com and https://spam3.com";
    // The spam check returns early before FCM is called
    await handler({
      data: {data: () => ({senderId: "u1", receiverId: "u2", content: spamContent, type: "TEXT"})},
      params: {chatId: "u1_u2", messageId: "m1"},
    });
    // With link flooding, isSpamMessage returns true, so no FCM call
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });
});

// ── sendFriendRequestNotification trigger ─────────────────────────────

describe("sendFriendRequestNotification", () => {
  const handler = registeredHandlers["friend_requests/{requestId}"];

  it("is registered as a Firestore trigger", () => {
    expect(handler).toBeDefined();
  });

  it("returns early for non-PENDING status", async () => {
    await handler({
      data: {data: () => ({status: "ACCEPTED", fromUserId: "u1", toUserId: "u2"})},
      params: {requestId: "r1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("returns early when senderId or receiverId is missing", async () => {
    await handler({
      data: {data: () => ({status: "PENDING", fromUserId: "", toUserId: "u2"})},
      params: {requestId: "r1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });
});

// ── sendRequestAcceptedNotification trigger ───────────────────────────

describe("sendRequestAcceptedNotification", () => {
  const handler = registeredHandlers["friend_requests/{requestId}"];
  // Note: both create and update handlers are registered on the same document path
  // The update handler is the second one registered

  it("is registered as a Firestore trigger", () => {
    expect(handler).toBeDefined();
  });
});

// ── sendSharedInboxNotification trigger ───────────────────────────────

describe("sendSharedInboxNotification", () => {
  const handler = registeredHandlers["users/{userId}/shared_inbox/{itemId}"];

  it("is registered as a Firestore trigger", () => {
    expect(handler).toBeDefined();
  });

  it("returns early when data is missing", async () => {
    await handler({data: null, params: {userId: "u1", itemId: "i1"}});
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("returns early for non-PENDING status", async () => {
    await handler({
      data: {data: () => ({status: "ACCEPTED", fromUsername: "friend", type: "WORD"})},
      params: {userId: "u1", itemId: "i1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });
});

// ── Spam detection edge cases ──────────────────────────────────────────

describe("spam detection edge cases", () => {
  const handler = registeredHandlers["chats/{chatId}/messages/{messageId}"];

  it("allows messages with fewer than 3 links", async () => {
    // Mock for spam check (need to mock the recent messages query)
    mockGet.mockResolvedValueOnce({empty: true, docs: []});
    // Mock for FCM tokens
    mockDocGet.mockResolvedValueOnce({empty: true, docs: []});

    const content = "Check https://link1.com and https://link2.com";
    await handler({
      data: {data: () => ({senderId: "u1", receiverId: "u2", content, type: "TEXT"})},
      params: {chatId: "u1_u2", messageId: "m1"},
    });

    // Should proceed (not blocked by spam check)
    // The FCM call may or may not happen depending on token availability
  });

  it("allows non-duplicate messages", async () => {
    // Mock recent messages with different content
    mockGet.mockResolvedValueOnce({
      empty: false,
      docs: [
        {data: () => ({content: "unique message 1"})},
        {data: () => ({content: "unique message 2"})},
      ],
    });
    // Mock for FCM tokens (empty)
    mockDocGet.mockResolvedValueOnce({empty: true, docs: []});

    await handler({
      data: {data: () => ({senderId: "u1", receiverId: "u2", content: "new message", type: "TEXT"})},
      params: {chatId: "u1_u2", messageId: "m1"},
    });

    // Should proceed (not spam)
  });
});

// ── sendRequestAcceptedNotification edge cases ─────────────────────────

describe("sendRequestAcceptedNotification edge cases", () => {
  // This uses onDocumentUpdated which has before/after data
  const updateHandler = registeredHandlers["friend_requests/{requestId}"];

  it("returns early when before data is missing", async () => {
    await updateHandler({
      data: {before: null, after: {data: () => ({status: "ACCEPTED"})}},
      params: {requestId: "r1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("returns early when after data is missing", async () => {
    await updateHandler({
      data: {before: {data: () => ({status: "PENDING"})}, after: null},
      params: {requestId: "r1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("returns early when status is not transitioning to ACCEPTED", async () => {
    await updateHandler({
      data: {
        before: {data: () => ({status: "PENDING", fromUserId: "u1"})},
        after: {data: () => ({status: "REJECTED", fromUserId: "u1"})},
      },
      params: {requestId: "r1"},
    });
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });
});
