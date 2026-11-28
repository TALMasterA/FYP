import {createHash} from "node:crypto";
import {HttpsError} from "firebase-functions/v2/https";
import {getFirestore} from "./helpers.js";
import {logger} from "./logger.js";

type Unit = "translatorCharacters" | "learningInputTokens" | "learningOutputTokens" | "speechTokens";
type Charge = {unit: Unit; quantity: number};
type Reservation = {
  uid: string;
  operation: string;
  requestId: unknown;
  charges: Charge[];
  deployment?: string;
};

const MAX_MONTHLY_NANODOLLARS = 1_999_999_999;
const REQUEST_RETENTION_MS = 7 * 24 * 60 * 60 * 1000;

function paused(): HttpsError {
  return new HttpsError("resource-exhausted",
    "This feature is temporarily unavailable. Its monthly allowance is paused or exhausted.");
}

function nonNegativeInteger(value: unknown): value is number {
  return typeof value === "number" && Number.isSafeInteger(value) && value >= 0;
}

/**
 * Claims a request and reserves its maximum provider exposure in one transaction.
 * This function runs before any provider call. Provider failures are never refunded.
 */
export async function reserveProviderAllowance(request: Reservation): Promise<void> {
  const {uid, operation, requestId, charges, deployment} = request;
  if (requestId !== undefined &&
      (typeof requestId !== "string" || !/^[A-Za-z0-9_-]{16,64}$/.test(requestId))) {
    throw new HttpsError("invalid-argument", "Invalid request ID.");
  }

  const now = new Date();
  const month = now.toISOString().slice(0, 7);
  try {
    const db = getFirestore();
    const policyRef = db.collection("provider_allowances").doc(month);
    const usageRef = db.collection("provider_usage").doc(month);
    const claim = requestId === undefined ? undefined : db.collection("provider_requests").doc(
      createHash("sha256").update(JSON.stringify([uid, operation, requestId])).digest("hex")
    );

    await db.runTransaction(async (tx) => {
      if (claim && (await tx.get(claim)).exists) {
        throw new HttpsError("already-exists", "This request was already submitted. Start a new request to try again.");
      }

      const policy = (await tx.get(policyRef)).data();
      const budget = policy?.budgetNanodollars;
      if (!policy || !nonNegativeInteger(budget) || budget > MAX_MONTHLY_NANODOLLARS) {
        throw paused();
      }
      if (deployment !== undefined && policy.learningDeployment !== deployment) throw paused();
      // Issued tokens let the SDK talk directly to Azure. Only enable issuance
      // while the bound Speech resource has a verified free-tier provider cutoff.
      if (operation === "getSpeechToken" && policy.speechF0Verified !== true) throw paused();

      const snapshot = await tx.get(usageRef);
      const usage = snapshot.exists ? snapshot.data() : {reservedNanodollars: 0};
      let reserved = usage?.reservedNanodollars;
      if (!usage || !nonNegativeInteger(reserved)) throw paused();
      const next = {...usage};

      for (const {unit, quantity} of charges) {
        const cap = policy[unit]?.maxUnits;
        const rate = policy[unit]?.nanodollarsPerUnit;
        const previous = usage[unit] === undefined ? 0 : usage[unit];
        if (!nonNegativeInteger(cap) || cap === 0 || !nonNegativeInteger(rate) ||
            !nonNegativeInteger(previous) || quantity > cap - previous ||
            (operation === "getSpeechToken" && rate !== 0)) throw paused();
        const amount = quantity * rate;
        if (!nonNegativeInteger(quantity) || !nonNegativeInteger(amount) || amount > budget - reserved) throw paused();
        reserved += amount;
        next[unit] = previous + quantity;
      }
      if (reserved > budget) throw paused();

      tx.set(usageRef, {...next, reservedNanodollars: reserved});
      if (claim) tx.create(claim, {expiresAt: new Date(now.getTime() + REQUEST_RETENTION_MS)});
    });
  } catch (error) {
    if (error instanceof HttpsError) throw error;
    logger.error("Provider allowance transaction failed", {operation});
    throw new HttpsError("unavailable", "This feature is temporarily unavailable. Please try again later.");
  }
}
