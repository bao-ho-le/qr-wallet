import { test, expect } from "@playwright/test";

test("TC1 - Load landing page", async ({ page }) => {
  await page.goto("/");

  await expect(page.getByRole("link", { name: /upload qr/i })).toBeVisible();
  await expect(page.getByRole("link", { name: /qr list/i })).toBeVisible();
});

test("TC2 - Navigate pages", async ({ page }) => {
  await page.goto("/");

  await page.getByRole("link", { name: /upload qr/i }).click();
  await expect(page).toHaveURL("/qr/upload");

  await page.goto("/");

  await page.getByRole("link", { name: /qr list/i }).click();
  await expect(page).toHaveURL("/qr/list");
});
