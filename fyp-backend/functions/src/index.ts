import {setGlobalOptions} from "firebase-functions";
import {defineSecret} from "firebase-functions/params";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import fetch from "node-fetch";

setGlobalOptions({maxInstances: 10});

const AZURE_SPEECH_KEY = defineSecret("AZURE_SPEECH_KEY");
const AZURE_SPEECH_REGION = defineSecret("AZURE_SPEECH_REGION");
const AZURE_TRANSLATOR_KEY = defineSecret("AZURE_TRANSLATOR_KEY");
const AZURE_TRANSLATOR_REGION = defineSecret("AZURE_TRANSLATOR_REGION");
const GENAI_BASE_URL = defineSecret("GENAI_BASE_URL");
const GENAI_API_VERSION = defineSecret("GENAI_API_VERSION");
const GENAI_API_KEY = defineSecret("GENAI_API_KEY");

const ENDPOINT = "https://api.cognitive.microsofttranslator.com";
const API_VERSION = "3.0";


function requireAuth(auth: unknown) {
  if (!auth) {
    throw new HttpsError(
      "unauthenticated", "Login required."
    );
  }
}

// ============ Reusable Validation Helpers ============

/**
 * Validates that a required string parameter is present and non-empty.
 * @param value The value to validate
 * @param paramName The parameter name for error messages
 * @returns The trimmed string value
 */
function requireString(value: unknown, paramName: string): string {
  const str = String(value ?? "").trim();
  if (!str) {
    throw new HttpsError("invalid-argument", `${paramName} is required`);
  }
  return str;
}

/**
 * Validates an optional string parameter, returning empty string if not provided.
 * @param value The value to validate
 * @returns The trimmed string value or empty string
 */
function optionalString(value: unknown): string {
  return value ? String(value).trim() : "";
}

/**
 * Validates array parameter with optional max length check.
 * @param value The value to validate
 * @param paramName The parameter name for error messages
 * @param maxLength Optional maximum array length
 * @returns The validated array
 */
function requireArray<T>(value: unknown, paramName: string, maxLength?: number): T[] {
  if (!Array.isArray(value) || value.length === 0) {
    throw new HttpsError("invalid-argument", `${paramName} is required`);
  }
  if (maxLength && value.length > maxLength) {
    throw new HttpsError("resource-exhausted", `${paramName} exceeds maximum of ${maxLength} items`);
  }
  return value as T[];
}

export const getSpeechToken = onCall(
  {secrets: [AZURE_SPEECH_KEY, AZURE_SPEECH_REGION]},
  async (request) => {
    requireAuth(request.auth);

    const key = AZURE_SPEECH_KEY.value();
    const region = AZURE_SPEECH_REGION.value();

    const url = `https://${region}.api.cognitive.microsoft.com/sts/v1.0/issueToken`;

    const resp = await fetch(url, {
      method: "POST",
      headers: {
        "Ocp-Apim-Subscription-Key": key,
        "Content-Length": "0",
      },
    });

    const token = await resp.text();
    if (!resp.ok) {
      throw new HttpsError(
        "internal", `Speech token HTTP ${resp.status}: ${token}`
      );
    }

    return {token, region};
  }
);

function buildTranslateUrl(params: { to: string; from?: string }) {
  const url = new URL(`${ENDPOINT}/translate`);
  url.searchParams.set("api-version", API_VERSION);
  url.searchParams.set("to", params.to);
  if (params.from) url.searchParams.set("from", params.from);
  return url.toString();
}

export const translateText = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
  async (request) => {
    requireAuth(request.auth);

    const text = requireString(request.data?.text, "text");
    const to = requireString(request.data?.to, "to");
    const from = optionalString(request.data?.from);

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = buildTranslateUrl({to, from: from || undefined});

    const resp = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        "Ocp-Apim-Subscription-Key": key,
        "Ocp-Apim-Subscription-Region": region,
      },
      body: JSON.stringify([{Text: text}]),
    });

    const bodyText = await resp.text();
    if (!resp.ok) {
      throw new HttpsError(
        "internal", `Translator HTTP ${resp.status}: ${bodyText}`
      );
    }

    const json = JSON.parse(bodyText);
    const translated = json?.[0]?.translations?.[0]?.text ?? "";
    return {translatedText: translated};
  }
);

export const translateTexts = onCall(
  {secrets: [AZURE_TRANSLATOR_KEY, AZURE_TRANSLATOR_REGION]},
  async (request) => {
    // No auth required - allows UI language translation for all users
    // Using validation helpers with max limit (250) for rate protection
    const texts = requireArray<unknown>(request.data?.texts, "texts", 250);
    const to = requireString(request.data?.to, "to");
    const from = optionalString(request.data?.from);

    const key = AZURE_TRANSLATOR_KEY.value();
    const region = AZURE_TRANSLATOR_REGION.value();

    const url = buildTranslateUrl({to, from: from || undefined});
    const reqBody = texts.map((t) => ({Text: String(t ?? "")}));

    const resp = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        "Ocp-Apim-Subscription-Key": key,
        "Ocp-Apim-Subscription-Region": region,
      },
      body: JSON.stringify(reqBody),
    });

    const bodyText = await resp.text();
    if (!resp.ok) {
      throw new HttpsError(
        "internal", `Translator HTTP ${resp.status}: ${bodyText}`
      );
    }

    const json = JSON.parse(bodyText);
    const translatedTexts: string[] = Array.isArray(json) ?
      json.map((item: any) => item?.translations?.[0]?.text ?? "") :
      [];

    return {translatedTexts};
  }
);

export const generateLearningContent = onCall(
  {
    secrets: [GENAI_BASE_URL, GENAI_API_VERSION, GENAI_API_KEY],
    timeoutSeconds: 300, // 5 minutes timeout for AI generation
  },
  async (request) => {
    requireAuth(request.auth);

    const deployment = requireString(request.data?.deployment, "deployment");
    const prompt = requireString(request.data?.prompt, "prompt");

    const baseUrl = GENAI_BASE_URL.value();
    const apiVersion = GENAI_API_VERSION.value();
    const apiKey = GENAI_API_KEY.value();

    const url = new URL(
      baseUrl.replace(/\/+$/, "") +
        `/deployments/${encodeURIComponent(deployment)}/chat/completions`
    );
    url.searchParams.set("api-version", apiVersion);

    const body = {
      messages: [
        { role: "system", content: "You are a helpful language learning assistant." },
        { role: "user", content: prompt },
      ],
      temperature: 1,
    };

    const resp = await fetch(url.toString(), {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "api-key": apiKey,
      },
      body: JSON.stringify(body),
    });

    const text = await resp.text();
    if (!resp.ok) {
      throw new HttpsError("internal", `GenAI HTTP ${resp.status}: ${text}`);
    }

    const json = JSON.parse(text);
    const content = json?.choices?.[0]?.message?.content ?? "";
    return { content };
  }
);