/**
 * Static guard: HTTPS functions must go through functionWrappers.ts.
 */
import {describe, expect, it} from "@jest/globals";
import * as fs from "fs";
import * as path from "path";

const SRC_DIR = path.resolve(__dirname, "..");
const WRAPPER_FILE = "functionWrappers.ts";

function sourceFiles(): string[] {
  return fs.readdirSync(SRC_DIR)
    .filter((file) => file.endsWith(".ts"))
    .filter((file) => file !== WRAPPER_FILE);
}

describe("App Check function wrapper guard", () => {
  it("keeps direct onCall and onRequest imports inside functionWrappers only", () => {
    const directHttpsImport = /import\s*\{[^}]*\b(onCall|onRequest)\b[^}]*\}\s*from\s*["']firebase-functions\/v2\/https["']/;
    const offenders = sourceFiles().filter((file) => {
      const text = fs.readFileSync(path.join(SRC_DIR, file), "utf8");
      return directHttpsImport.test(text);
    });

    expect(offenders).toEqual([]);
  });

  it("keeps callable functions on the App Check wrapper", () => {
    const wrapper = fs.readFileSync(path.join(SRC_DIR, WRAPPER_FILE), "utf8");
    expect(wrapper).toContain("enforceAppCheck: true");

    const offenders = sourceFiles().filter((file) => {
      const text = fs.readFileSync(path.join(SRC_DIR, file), "utf8");
      return /\b(onCall|onRequest)\s*\(/.test(text);
    });

    expect(offenders).toEqual([]);
  });
});
