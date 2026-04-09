module.exports = {
  root: true,
  env: {
    es6: true,
    node: true,
  },
  extends: [
    "eslint:recommended",
    "plugin:import/errors",
    "plugin:import/warnings",
    "plugin:import/typescript",
    "google",
    "plugin:@typescript-eslint/recommended",
  ],
  parser: "@typescript-eslint/parser",
  parserOptions: {
    tsconfigRootDir: __dirname,
    project: ["tsconfig.json", "tsconfig.dev.json"],
    sourceType: "module",
  },
  ignorePatterns: [
    "/lib/**/*", // Ignore built files.
    "/generated/**/*", // Ignore generated files.
    "/coverage/**/*", // Ignore generated coverage reports.
    "jest.config.js",
  ],
  plugins: [
    "@typescript-eslint",
    "import",
  ],
  rules: {
    "quotes": ["error", "double"],
    "linebreak-style": "off",
    "import/no-unresolved": 0,
    "indent": ["error", 2],
    "require-jsdoc": "off",
    "valid-jsdoc": "off",
    "max-len": ["error", {"code": 140}],
    "operator-linebreak": "off",
    "@typescript-eslint/no-explicit-any": "off",
    "no-constant-condition": "off",
  },
};
