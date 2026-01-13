import {setGlobalOptions} from "firebase-functions";
import {defineSecret} from "firebase-functions/params";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import fetch from "node-fetch";

setGlobalOptions({maxInstances: 10});

const AZURE_TRANSLATOR_KEY = defineSecret("AZURE_TRANSLATOR_KEY");
const AZURE_TRANSLATOR_REGION = defineSecret("AZURE_TRANSLATOR_REGION");

const ENDPOINT = "https://api.cognitive.microsofttranslator.com";
const API_VERSION = "3.0";


function requireAuth(auth: unknown) {
  if (!auth) {
    throw new HttpsError(
      "unauthenticated", "Login required."
    );
  }
}

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

    const text = String(request.data?.text ?? "");
    const to = String(request.data?.to ?? "");
    const from = request.data?.from ? String(request.data.from) : "";

    if (!text.trim()) {
      throw new HttpsError(
        "invalid-argument", "text is required"
      );
    }
    if (!to.trim()) {
      throw new HttpsError(
        "invalid-argument", "to is required"
      );
    }

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
    // requireAuth(request.auth);

    const texts: unknown[] =
    Array.isArray(request.data?.texts) ? request.data.texts : [];

    if (texts.length > 150) {
      throw new HttpsError("resource-exhausted", "Too many UI strings.");
    }

    const to = String(request.data?.to ?? "");
    const from = request.data?.from ? String(request.data.from) : "";

    if (texts.length === 0) {
      throw new HttpsError(
        "invalid-argument", "texts is required"
      );
    }
    if (!to.trim()) throw new HttpsError("invalid-argument", "to is required");

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
