import {getFirestore} from "../helpers.js";
import {reserveProviderAllowance} from "../providerAllowance.js";

jest.mock("../helpers.js", () => ({getFirestore: jest.fn()}));
jest.mock("../logger.js", () => ({logger: {error: jest.fn()}}));

const policy = (month = "2026-11") => "provider_allowances/" + month;
const usage = (month = "2026-11") => "provider_usage/" + month;
const request = {
  uid: "user-1",
  operation: "translateText",
  requestId: "stable-test-request",
  charges: [{unit: "translatorCharacters" as const, quantity: 10}],
};

function setUpTransaction() {
  const documents = new Map<string, object>();
  const tx = {
    get: jest.fn(async (path: string) => ({exists: documents.has(path), data: () => documents.get(path)})),
    set: jest.fn((path: string, data: object) => documents.set(path, data)),
    create: jest.fn((path: string, data: object) => documents.set(path, data)),
  };
  const runTransaction = jest.fn(async (fn) => fn(tx));
  (getFirestore as jest.Mock).mockReturnValue({
    collection: (name: string) => ({doc: (doc: string) => name + "/" + doc}),
    runTransaction,
  });
  return {documents, tx, runTransaction};
}

const translationPolicy = {
  budgetNanodollars: 100,
  translatorCharacters: {maxUnits: 100, nanodollarsPerUnit: 5},
};

beforeEach(() => {
  const now = Date.parse("2026-11-30T23:59:59Z");
  jest.useFakeTimers({now});
});
afterEach(() => {
  jest.useRealTimers();
  jest.clearAllMocks();
});

it("retains the full reservation after an uncertain outcome and suppresses a duplicate after rollover", async () => {
  const {documents, tx} = setUpTransaction();
  documents.set(policy(), translationPolicy);
  await reserveProviderAllowance(request);
  expect(documents.get(usage())).toEqual({translatorCharacters: 10, reservedNanodollars: 50});
  expect(tx.create).toHaveBeenCalledTimes(1);

  jest.setSystemTime(Date.parse("2026-12-01T00:00:01Z"));
  await expect(reserveProviderAllowance(request)).rejects.toMatchObject({code: "already-exists"});
  documents.set(policy("2026-12"), translationPolicy);
  await reserveProviderAllowance({...request, requestId: undefined});
  expect(documents.get(usage("2026-12"))).toEqual({translatorCharacters: 10, reservedNanodollars: 50});
});

it.each([
  [undefined, undefined],
  [{budgetNanodollars: 2_000_000_000}, undefined],
  [translationPolicy, {reservedNanodollars: "0"}],
  [translationPolicy, {reservedNanodollars: 51}],
  [translationPolicy, {reservedNanodollars: 0, translatorCharacters: 91}],
  [translationPolicy, {reservedNanodollars: 0, translatorCharacters: -1}],
  [{...translationPolicy, translatorCharacters: {maxUnits: 100}}, undefined],
])("rejects missing, invalid, or exhausted allowance state", async (configured, recorded) => {
  const {documents, tx} = setUpTransaction();
  if (configured) documents.set(policy(), configured);
  if (recorded) documents.set(usage(), recorded);
  await expect(reserveProviderAllowance(request)).rejects.toMatchObject({code: "resource-exhausted"});
  expect(tx.set).not.toHaveBeenCalled();
  expect(tx.create).not.toHaveBeenCalled();
});

it("shares the cash balance between translation and learning and enforces the approved deployment", async () => {
  const {documents} = setUpTransaction();
  documents.set(policy(), {
    ...translationPolicy,
    learningDeployment: "demo-model",
    learningInputTokens: {maxUnits: 100, nanodollarsPerUnit: 1},
    learningOutputTokens: {maxUnits: 100, nanodollarsPerUnit: 2},
  });
  const learning = {
    ...request,
    operation: "generateLearningContent",
    deployment: "demo-model",
    charges: [
      {unit: "learningInputTokens" as const, quantity: 10},
      {unit: "learningOutputTokens" as const, quantity: 21},
    ],
  };
  await reserveProviderAllowance(request);
  await expect(reserveProviderAllowance({...learning, deployment: "another-model"}))
    .rejects.toMatchObject({code: "resource-exhausted"});
  await expect(reserveProviderAllowance(learning)).rejects.toMatchObject({code: "resource-exhausted"});
  expect(documents.get(usage())).toEqual({translatorCharacters: 10, reservedNanodollars: 50});
});

it("only issues tokens with an explicitly verified zero-cost Speech tier", async () => {
  const {documents} = setUpTransaction();
  const speech = {...request, operation: "getSpeechToken", charges: [{unit: "speechTokens" as const, quantity: 1}]};
  documents.set(policy(), {budgetNanodollars: 0, speechTokens: {maxUnits: 1, nanodollarsPerUnit: 0}});
  await expect(reserveProviderAllowance(speech)).rejects.toMatchObject({code: "resource-exhausted"});
  documents.set(policy(), {budgetNanodollars: 0, speechTokens: {maxUnits: 1, nanodollarsPerUnit: 1}, speechF0Verified: true});
  await expect(reserveProviderAllowance(speech)).rejects.toMatchObject({code: "resource-exhausted"});
  documents.set(policy(), {budgetNanodollars: 0, speechTokens: {maxUnits: 1, nanodollarsPerUnit: 0}, speechF0Verified: true});
  await reserveProviderAllowance(speech);
  expect(documents.get(usage())).toEqual({speechTokens: 1, reservedNanodollars: 0});
});

it("fails closed before dispatch on malformed request ID or Firestore failure", async () => {
  const {runTransaction} = setUpTransaction();
  await expect(reserveProviderAllowance({...request, requestId: null})).rejects.toMatchObject({code: "invalid-argument"});
  expect(runTransaction).not.toHaveBeenCalled();
  runTransaction.mockRejectedValueOnce(new Error("Firestore offline"));
  await expect(reserveProviderAllowance(request)).rejects.toMatchObject({code: "unavailable"});
});
