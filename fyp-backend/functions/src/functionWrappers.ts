/**
 * Central wrappers for HTTPS Cloud Functions.
 *
 * Callable functions must use onAppCheckCall so App Check enforcement cannot
 * be accidentally omitted when new endpoints are added.
 */
import {onCall, onRequest} from "firebase-functions/v2/https";

export function onAppCheckCall(
  options: Record<string, unknown>,
  handler: (request: any) => any
) {
  return onCall({...options, enforceAppCheck: true} as any, handler as any);
}

export function onPublicRequest(
  options: Record<string, unknown>,
  handler: (request: any, response: any) => any
) {
  return onRequest(options as any, handler as any);
}
