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
      expect(rules).toContain(`!(\"${field}\" in request.resource.data)`);
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
});
