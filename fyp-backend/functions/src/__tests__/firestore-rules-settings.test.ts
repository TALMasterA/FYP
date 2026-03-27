import fs from "fs";
import path from "path";

describe("firestore.rules settings validation", () => {
  const rulesPath = path.resolve(__dirname, "../../../firestore.rules");
  const rules = fs.readFileSync(rulesPath, "utf8");

  it("validates all notification preference fields as boolean", () => {
    const requiredBooleanFields = [
      "notifyNewMessages",
      "notifyFriendRequests",
      "notifyRequestAccepted",
      "notifySharedInbox",
      "inAppBadgeMessages",
      "inAppBadgeFriendRequests",
      "inAppBadgeSharedInbox",
    ];

    requiredBooleanFields.forEach((field) => {
      expect(rules).toContain(`!("${field}" in request.resource.data)`);
      expect(rules).toContain(`request.resource.data.${field} is bool`);
    });
  });

  it("keeps numeric guards for historyViewLimit and fontSizeScale", () => {
    expect(rules).toContain("request.resource.data.historyViewLimit is int");
    expect(rules).toContain("request.resource.data.historyViewLimit >= 30");
    expect(rules).toContain("request.resource.data.historyViewLimit <= 60");

    expect(rules).toContain("request.resource.data.fontSizeScale is number");
    expect(rules).toContain("request.resource.data.fontSizeScale >= 0.5");
    expect(rules).toContain("request.resource.data.fontSizeScale <= 2.0");
  });

  it("keeps private settings owner-only and public profile readable for authenticated users", () => {
    expect(rules).toContain("match /users/{userId}/profile/settings {");
    expect(rules).toContain("allow read: if request.auth != null && request.auth.uid == userId;");
    expect(rules).toContain("match /users/{userId}/profile/public {");
    expect(rules).toContain("allow read: if request.auth != null;");
  });

  it("requires friendship and no block relation for shared inbox writes", () => {
    expect(rules).toContain("function canShareToInboxRecipient(senderId, recipientId)");
    expect(rules).toContain("exists(/databases/$(database)/documents/users/$(recipientId)/friends/$(senderId))");
    expect(rules).toContain("exists(/databases/$(database)/documents/users/$(senderId)/friends/$(recipientId))");
    expect(rules).toContain("!exists(/databases/$(database)/documents/users/$(senderId)/blocked_users/$(recipientId))");
    expect(rules).toContain("!exists(/databases/$(database)/documents/users/$(recipientId)/blocked_users/$(senderId))");
    expect(rules).toContain("&& canShareToInboxRecipient(request.auth.uid, userId)");
  });

  it("requires mutual friendship and no blocks for chat message creates", () => {
    expect(rules).toContain("function canWriteChatContent()");
    expect(rules).toContain("exists(/databases/$(database)/documents/users/$(request.auth.uid)/friends/$(otherId))");
    expect(rules).toContain("exists(/databases/$(database)/documents/users/$(otherId)/friends/$(request.auth.uid))");
    expect(rules).toContain("!exists(/databases/$(database)/documents/users/$(request.auth.uid)/blocked_users/$(otherId))");
    expect(rules).toContain("!exists(/databases/$(database)/documents/users/$(otherId)/blocked_users/$(request.auth.uid))");
    expect(rules).toContain("&& canWriteChatContent()");
  });

  it("keeps chat metadata writes participant-scoped", () => {
    expect(rules).toContain("function canWriteChatMetadata()");
    expect(rules).toContain("return isParticipantFromChatId();");
    expect(rules).toContain("let userIds = chatId.split('_');");
    expect(rules).toContain("userIds.size() == 2");
  });
});
