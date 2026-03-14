/** @type {import('ts-jest').JestConfigWithTsJest} */
module.exports = {
  preset: "ts-jest",
  testEnvironment: "node",
  roots: ["<rootDir>/src"],
  testMatch: ["**/__tests__/**/*.test.ts"],
  moduleNameMapper: {
    "^(\\.\\.?\\/.*)\\.js$": "$1",
  },
  transform: {
    "^.+\\.ts$": ["ts-jest", {
      diagnostics: {ignoreCodes: [151002]},
    }],
  },
};
