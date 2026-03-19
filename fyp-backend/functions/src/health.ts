/**
 * Health and readiness checks.
 */
import {onRequest} from "firebase-functions/v2/https";
import {
  GENAI_BASE_URL,
  GENAI_API_KEY,
  GENAI_API_VERSION,
  validateGenAiConfig,
} from "./helpers.js";
import {logger} from "./logger.js";

export const healthcheck = onRequest(
  {
    secrets: [GENAI_BASE_URL, GENAI_API_VERSION, GENAI_API_KEY],
  },
  async (request, response) => {
    if (request.method !== "GET") {
      response.status(405).json({
        status: "error",
        message: "Method not allowed",
      });
      return;
    }

    try {
      validateGenAiConfig({
        baseUrl: GENAI_BASE_URL.value(),
        apiVersion: GENAI_API_VERSION.value(),
        apiKey: GENAI_API_KEY.value(),
      });

      response.status(200).json({
        status: "ok",
        timestamp: new Date().toISOString(),
        checks: {
          genAiConfig: "ok",
        },
      });
    } catch (error: any) {
      logger.error("healthcheck failed", {
        message: error?.message,
      });

      response.status(500).json({
        status: "error",
        timestamp: new Date().toISOString(),
        checks: {
          genAiConfig: "error",
        },
        message: "Service is misconfigured",
      });
    }
  }
);
