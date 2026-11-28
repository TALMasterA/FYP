/**
 * AI-powered learning content generation Cloud Function.
 *
 * Uses Azure OpenAI to generate language-learning exercises,
 * with per-user rate limiting via Firestore.
 */
import {HttpsError} from "firebase-functions/v2/https";
import {onDocumentWritten} from "firebase-functions/v2/firestore";
import fetch from "node-fetch";
import * as admin from "firebase-admin";
import {
  requireAuth,
  requireString,
  enforceRateLimit,
  validateGenAiConfig,
  getFirestore,
  GENAI_BASE_URL,
  GENAI_API_VERSION,
  GENAI_API_KEY,
} from "./helpers.js";
import {onAppCheckCall} from "./functionWrappers.js";
import {logger} from "./logger.js";
import {reserveProviderAllowance} from "./providerAllowance.js";

const LANG_CODE_RE = /^[a-z]{2}(-[A-Z]{2})?$/;
const DEFAULT_DEPLOYMENT = "gpt-6-luna";
const MAX_PROMPT_BYTES = 32_768;
const MAX_COMPLETION_TOKENS = 4096;
const INPUT_FRAMING_TOKEN_ALLOWANCE = 256;
const SYSTEM_PROMPT = "You are a helpful language learning assistant.";

async function getLanguageHistoryCount(uid: string, languageCode: string): Promise<number> {
  const historyRef = getFirestore()
    .collection("users")
    .doc(uid)
    .collection("history");

  const [sourceSnap, targetSnap, sameSnap] = await Promise.all([
    historyRef.where("sourceLang", "==", languageCode).count().get(),
    historyRef.where("targetLang", "==", languageCode).count().get(),
    historyRef
      .where("sourceLang", "==", languageCode)
      .where("targetLang", "==", languageCode)
      .count()
      .get(),
  ]);

  const sourceCount = Number(sourceSnap.data().count ?? 0);
  const targetCount = Number(targetSnap.data().count ?? 0);
  const sameCount = Number(sameSnap.data().count ?? 0);
  return Math.max(0, sourceCount + targetCount - sameCount);
}

/**
 * Maintains server-owned quiz version metadata for each learning-sheet language pair.
 *
 * The version is computed from Firestore history on the server and stored under:
 * users/{uid}/quiz_versions/{primary}__{target}
 */
export const syncQuizVersionFromLearningSheet = onDocumentWritten(
  {
    document: "users/{userId}/learning_sheets/{sheetId}",
    region: "us-central1",
  },
  async (event) => {
    const uid = event.params.userId;
    const sheetId = event.params.sheetId;
    const versionRef = getFirestore()
      .collection("users")
      .doc(uid)
      .collection("quiz_versions")
      .doc(sheetId);

    const after = event.data?.after;
    if (!after || !after.exists) {
      try {
        await versionRef.delete();
      } catch {
        // Best-effort cleanup if sheet is removed.
      }
      return;
    }

    const data = after.data() ?? {};
    let primaryCode = String(data.primaryLanguageCode ?? "").trim();
    let targetCode = String(data.targetLanguageCode ?? "").trim();

    if ((!primaryCode || !targetCode) && sheetId.includes("__")) {
      const parts = sheetId.split("__", 2);
      if (parts.length === 2) {
        if (!primaryCode) primaryCode = parts[0];
        if (!targetCode) targetCode = parts[1];
      }
    }

    if (!LANG_CODE_RE.test(primaryCode) || !LANG_CODE_RE.test(targetCode)) {
      logger.warn("syncQuizVersionFromLearningSheet: invalid language codes", {
        uid,
        sheetId,
        primaryCode,
        targetCode,
      });
      return;
    }

    let historyCount: number;
    try {
      historyCount = await getLanguageHistoryCount(uid, targetCode);
    } catch (err: any) {
      logger.error("syncQuizVersionFromLearningSheet: failed to count history", {
        uid,
        sheetId,
        targetCode,
        error: err?.message,
      });
      return;
    }

    await versionRef.set({
      primaryLanguageCode: primaryCode,
      targetLanguageCode: targetCode,
      historyCount,
      sourceSheetId: sheetId,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    }, {merge: true});
  }
);

export const generateLearningContent = onAppCheckCall(
  {
    secrets: [GENAI_BASE_URL, GENAI_API_VERSION, GENAI_API_KEY],
    maxInstances: 2,
    concurrency: 2,
    timeoutSeconds: 90,
  },
  async (request) => {
    try {
      requireAuth(request.auth);

      const uid = (request.auth as {uid: string}).uid;

      const requestedDeployment = requireString(request.data?.deployment, "deployment");
      // Existing Android builds use this ID for sheets, quizzes, and Word Bank.
      const deployment = requestedDeployment === "gpt-5-mini" ?
        process.env.GENAI_DEFAULT_DEPLOYMENT?.trim() || DEFAULT_DEPLOYMENT : requestedDeployment;
      const prompt = requireString(request.data?.prompt, "prompt");

      const promptBytes = Buffer.byteLength(prompt, "utf8");
      if (promptBytes > MAX_PROMPT_BYTES) {
        throw new HttpsError("invalid-argument", "The learning prompt is too long.");
      }
      await enforceRateLimit(uid);

      let baseUrl: string;
      let apiVersion: string;
      let apiKey: string;
      try {
        const config = validateGenAiConfig({
          baseUrl: GENAI_BASE_URL.value(),
          apiVersion: GENAI_API_VERSION.value(),
          apiKey: GENAI_API_KEY.value(),
        });
        baseUrl = config.baseUrl;
        apiVersion = config.apiVersion;
        apiKey = config.apiKey;
      } catch (configError: any) {
        logger.error("GenAI configuration validation failed", {
          uid,
          hasBaseUrl: !!GENAI_BASE_URL.value(),
          hasApiVersion: !!GENAI_API_VERSION.value(),
          hasApiKey: !!GENAI_API_KEY.value(),
          message: configError?.message,
        });
        throw configError;
      }

      let url: URL;
      try {
        url = new URL(
          baseUrl.replace(/\/+$/, "") +
            `/openai/deployments/${encodeURIComponent(deployment)}/chat/completions`
        );
        url.searchParams.set("api-version", apiVersion);
      } catch (urlError: any) {
        logger.error("Invalid GENAI_BASE_URL", {
          baseUrl,
          error: urlError?.message,
          uid,
        });
        throw new HttpsError(
          "failed-precondition",
          "AI service URL is misconfigured. Please contact support."
        );
      }

      await reserveProviderAllowance({
        uid,
        operation: "generateLearningContent",
        requestId: request.data?.requestId,
        deployment,
        charges: [
          {unit: "learningInputTokens",
            quantity: promptBytes + Buffer.byteLength(SYSTEM_PROMPT, "utf8") + INPUT_FRAMING_TOKEN_ALLOWANCE},
          {unit: "learningOutputTokens", quantity: MAX_COMPLETION_TOKENS},
        ],
      });

      logger.info("generateLearningContent: calling GenAI", {
        uid,
        deployment,
        promptLength: prompt.length,

      });

      const body = {
        messages: [
          {role: "system", content: SYSTEM_PROMPT},
          {role: "user", content: prompt},
        ],
        temperature: 1,
        max_completion_tokens: MAX_COMPLETION_TOKENS,
      };

      let resp;
      try {
        resp = await fetch(url.toString(), {
          timeout: 75_000,
          redirect: "error",
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "api-key": apiKey,
          },
          body: JSON.stringify(body),
        });
      } catch (fetchError: any) {
        logger.error("Network error calling GenAI API", {
          error: fetchError?.message,
          deployment,
        });
        throw new HttpsError(
          "unavailable",
          "Unable to reach AI service. Please check your internet connection and try again."
        );
      }

      const text = await resp.text();
      if (!resp.ok) {
        logger.error("GenAI API error", {
          status: resp.status,
          statusText: resp.statusText,
          errorPreview: text.substring(0, 500),
          deployment,
        });

        // Provide more specific error messages based on status code
        if (resp.status === 401 || resp.status === 403) {
          throw new HttpsError(
            "failed-precondition",
            "AI service authentication failed. Please contact support."
          );
        } else if (resp.status === 429) {
          throw new HttpsError(
            "resource-exhausted",
            "AI service rate limit exceeded. Please try again in a few minutes."
          );
        } else if (resp.status === 404) {
          logger.error("Azure OpenAI deployment not found", {deployment, baseUrl});
          throw new HttpsError(
            "not-found",
            "AI model configuration not found. Please contact support."
          );
        } else if (resp.status >= 500) {
          throw new HttpsError(
            "unavailable",
            "AI service is temporarily unavailable. Please try again later."
          );
        }

        throw new HttpsError(
          "internal",
          "AI content generation failed. Please try again."
        );
      }

      let json;
      try {
        json = JSON.parse(text);
      } catch (parseError: any) {
        logger.error("Failed to parse GenAI response", {
          error: parseError?.message,
          responsePreview: text.substring(0, 200),
        });
        throw new HttpsError(
          "internal",
          "Invalid response from AI service. Please try again."
        );
      }

      const content = json?.choices?.[0]?.message?.content ?? "";

      if (!content) {
        logger.error("Empty content from GenAI", {
          hasChoices: !!json?.choices,
          choicesLength: json?.choices?.length,
          firstChoice: json?.choices?.[0],
        });
        throw new HttpsError(
          "internal",
          "No content generated. Please try again."
        );
      }

      const model = typeof json?.model === "string" ? json.model.trim() : "";
      return model && model.length <= 128 ? {content, model} : {content};
    } catch (e: any) {
      // Re-throw HttpsErrors as-is (they already have code + message)
      if (e instanceof HttpsError) throw e;
      // Wrap any unexpected error with its message so it appears in the client
      logger.error("generateLearningContent: unexpected error", {
        message: e?.message,
        stack: e?.stack?.substring(0, 500),
      });
      throw new HttpsError(
        "internal",
        "Generation failed. Please try again."
      );
    }
  }
);
