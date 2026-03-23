/**
 * Unit tests for index.ts entrypoint wiring.
 */

const mockSetGlobalOptions = jest.fn();
const mockInitializeApp = jest.fn();

const mockExports = {
  getSpeechToken: jest.fn(),
  translateText: jest.fn(),
  translateTexts: jest.fn(),
  detectLanguage: jest.fn(),
  generateLearningContent: jest.fn(),
  syncQuizVersionFromLearningSheet: jest.fn(),
  awardQuizCoins: jest.fn(),
  spendCoins: jest.fn(),
  sendChatNotification: jest.fn(),
  sendFriendRequestNotification: jest.fn(),
  sendRequestAcceptedNotification: jest.fn(),
  sendSharedInboxNotification: jest.fn(),
  pruneStaleTokens: jest.fn(),
  pruneStaleRateLimits: jest.fn(),
  repairFriendsData: jest.fn(),
  healthcheck: jest.fn(),
};

jest.mock("firebase-functions/v2", () => ({
  setGlobalOptions: mockSetGlobalOptions,
}));

jest.mock("firebase-admin", () => ({
  initializeApp: mockInitializeApp,
}));

jest.mock("../translation.js", () => ({
  getSpeechToken: mockExports.getSpeechToken,
  translateText: mockExports.translateText,
  translateTexts: mockExports.translateTexts,
  detectLanguage: mockExports.detectLanguage,
}));

jest.mock("../learning.js", () => ({
  generateLearningContent: mockExports.generateLearningContent,
  syncQuizVersionFromLearningSheet: mockExports.syncQuizVersionFromLearningSheet,
}));

jest.mock("../coins.js", () => ({
  awardQuizCoins: mockExports.awardQuizCoins,
  spendCoins: mockExports.spendCoins,
}));

jest.mock("../notifications.js", () => ({
  sendChatNotification: mockExports.sendChatNotification,
  sendFriendRequestNotification: mockExports.sendFriendRequestNotification,
  sendRequestAcceptedNotification: mockExports.sendRequestAcceptedNotification,
  sendSharedInboxNotification: mockExports.sendSharedInboxNotification,
}));

jest.mock("../maintenance.js", () => ({
  pruneStaleTokens: mockExports.pruneStaleTokens,
  pruneStaleRateLimits: mockExports.pruneStaleRateLimits,
  repairFriendsData: mockExports.repairFriendsData,
}));

jest.mock("../health.js", () => ({
  healthcheck: mockExports.healthcheck,
}));

describe("functions index entrypoint", () => {
  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
  });

  it("initializes admin and sets global options at module load", async () => {
    await import("../index.js");

    expect(mockInitializeApp).toHaveBeenCalledTimes(1);
    expect(mockSetGlobalOptions).toHaveBeenCalledWith({maxInstances: 10});
  });

  it("re-exports all expected function handlers", async () => {
    const mod = await import("../index.js");

    expect(mod.getSpeechToken).toBe(mockExports.getSpeechToken);
    expect(mod.translateText).toBe(mockExports.translateText);
    expect(mod.translateTexts).toBe(mockExports.translateTexts);
    expect(mod.detectLanguage).toBe(mockExports.detectLanguage);

    expect(mod.generateLearningContent).toBe(mockExports.generateLearningContent);
    expect(mod.syncQuizVersionFromLearningSheet).toBe(mockExports.syncQuizVersionFromLearningSheet);

    expect(mod.awardQuizCoins).toBe(mockExports.awardQuizCoins);
    expect(mod.spendCoins).toBe(mockExports.spendCoins);

    expect(mod.sendChatNotification).toBe(mockExports.sendChatNotification);
    expect(mod.sendFriendRequestNotification).toBe(mockExports.sendFriendRequestNotification);
    expect(mod.sendRequestAcceptedNotification).toBe(mockExports.sendRequestAcceptedNotification);
    expect(mod.sendSharedInboxNotification).toBe(mockExports.sendSharedInboxNotification);

    expect(mod.pruneStaleTokens).toBe(mockExports.pruneStaleTokens);
    expect(mod.pruneStaleRateLimits).toBe(mockExports.pruneStaleRateLimits);
    expect(mod.repairFriendsData).toBe(mockExports.repairFriendsData);

    expect(mod.healthcheck).toBe(mockExports.healthcheck);
  });
});
