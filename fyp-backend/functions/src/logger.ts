/**
 * Structured logging utility for Cloud Functions.
 *
 * Wraps console methods with severity levels and structured metadata
 * so logs are properly parsed by Cloud Logging / Error Reporting.
 */

export type Severity = "INFO" | "WARN" | "ERROR" | "DEBUG";

interface LogEntry {
  severity: Severity;
  message: string;
  [key: string]: unknown;
}

function emit(entry: LogEntry): void {
  const {severity, ...rest} = entry;
  // Cloud Logging recognises JSON lines with a "severity" field
  const payload = {severity, ...rest};
  switch (severity) {
  case "ERROR":
    console.error(JSON.stringify(payload));
    break;
  case "WARN":
    console.warn(JSON.stringify(payload));
    break;
  case "DEBUG":
    console.debug(JSON.stringify(payload));
    break;
  default:
    console.log(JSON.stringify(payload));
  }
}

export const logger = {
  info(message: string, meta?: Record<string, unknown>): void {
    emit({severity: "INFO", message, ...meta});
  },
  warn(message: string, meta?: Record<string, unknown>): void {
    emit({severity: "WARN", message, ...meta});
  },
  error(message: string, meta?: Record<string, unknown>): void {
    emit({severity: "ERROR", message, ...meta});
  },
  debug(message: string, meta?: Record<string, unknown>): void {
    emit({severity: "DEBUG", message, ...meta});
  },
};
