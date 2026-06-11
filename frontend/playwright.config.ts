import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",

  use: {
    baseURL: "http://localhost:3000",
    headless: false,
    trace: "on-first-retry",
    screenshot: "only-on-failure",
  },

  webServer: {
    command: "npm run dev",
    url: "http://localhost:3000",
  },
});
