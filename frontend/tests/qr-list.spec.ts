import { test, expect } from "@playwright/test";
import { createTestQR } from "./qr.seed";

test("TC9 - Load QR list", async ({ page }) => {
  await page.goto("/qr/list");

  await expect(page.getByText(/qr list/i)).toBeVisible();
});

test("TC10 - Refresh list", async ({ page, request }) => {
  // seed data để list chắc chắn có item
  await createTestQR(request);

  await page.goto("/qr/list");

  // chờ API + render UI
  await page.waitForLoadState("networkidle");

  // đảm bảo list đã render
  await expect(page.locator(".record-list")).toBeVisible();

  // click refresh
  await page.getByRole("button", { name: /refresh/i }).click();

  // chờ reload data
  await page.waitForLoadState("networkidle");

  // verify vẫn có list
  await expect(page.locator(".record-list")).toBeVisible();

  //verify có ít nhất 1 item (cái vừa tạo)
  const afterCount = await page.locator(".record-item").count();
  expect(afterCount).toBeGreaterThanOrEqual(1);
});

test("TC11 - Navigate detail", async ({ page, request }) => {
  const qr = await createTestQR(request);

  await page.goto("/qr/list");
  await page.waitForLoadState("networkidle");

  // tìm đúng item theo UNIQUE field (accountNo là tốt nhất)
  const item = page.locator(".record-item").filter({ hasText: qr.accountNo });

  await expect(item).toBeVisible();

  await item.getByRole("link", { name: /details/i }).click();

  await expect(page).toHaveURL(/\/qr\/get\?id=/);
});
