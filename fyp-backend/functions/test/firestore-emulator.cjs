const assert = require("node:assert/strict");
const fs = require("node:fs");
const {after, before, test} = require("node:test");
const {assertFails, initializeTestEnvironment} = require("@firebase/rules-unit-testing");
const admin = require("firebase-admin");

const projectId = "demo-talknlearn";
if (!/^127\.0\.0\.1:\d+$/.test(process.env.FIRESTORE_EMULATOR_HOST ?? "")) {
  throw new Error("Run these tests through the loopback Firestore emulator.");
}

let environment;
let app;
let reserve;
const document = "users/owner/profile/settings";
const month = new Date().toISOString().slice(0, 7);
const policy = "provider_allowances/" + month;

before(async () => {
  environment = await initializeTestEnvironment({
    projectId,
    firestore: {rules: fs.readFileSync("../firestore.rules", "utf8")},
  });
  app = admin.initializeApp({projectId});
  ({reserveProviderAllowance: reserve} = require("../lib/providerAllowance.js"));
});

after(async () => {
  await environment?.cleanup();
  await app?.delete();
});

test("owner can create and read valid settings", async () => {
  const ref = environment.authenticatedContext("owner").firestore().doc(document);
  await ref.set({historyViewLimit: 45, fontSizeScale: 1.2});
  assert.equal((await ref.get()).data().historyViewLimit, 45);
});

test("other accounts cannot read or write owner settings", async () => {
  const ref = environment.authenticatedContext("other").firestore().doc(document);
  await assertFails(ref.get());
  await assertFails(ref.set({historyViewLimit: 45}));
});

test("owner cannot write out-of-range settings", async () => {
  const ref = environment.authenticatedContext("owner").firestore().doc(document);
  await assertFails(ref.set({historyViewLimit: 61}));
});

test("clients cannot read or write allowance, usage, claim or rate-limit documents", async () => {
  const db = environment.authenticatedContext("owner").firestore();
  for (const path of [policy, "provider_usage/" + month, "provider_requests/claim", "rate_limits/owner"]) {
    await assertFails(db.doc(path).get());
    await assertFails(db.doc(path).set({reservedNanodollars: 0}));
  }
});

test("shared monthly reservation remains atomic across users and duplicate retries", async () => {
  const db = admin.firestore();
  await db.doc(policy).set({
    budgetNanodollars: 100,
    translatorCharacters: {maxUnits: 100, nanodollarsPerUnit: 1},
  });
  const spend = (uid) => reserve({
    operation: "translateText",
    uid,
    requestId: "synthetic-request-id",
    charges: [{unit: "translatorCharacters", quantity: 100}],
  });
  const accounts = ["first", "second"];
  const results = await Promise.allSettled(accounts.map(spend));
  const succeeded = results.findIndex((item) => item.status === "fulfilled");
  assert.notEqual(succeeded, -1);
  assert.equal(results[1 - succeeded].reason.code, "resource-exhausted");
  await assert.rejects(spend(accounts[succeeded]), {code: "already-exists"});
  assert.deepEqual((await db.doc("provider_usage/" + month).get()).data(), {
    reservedNanodollars: 100,
    translatorCharacters: 100,
  });
});
