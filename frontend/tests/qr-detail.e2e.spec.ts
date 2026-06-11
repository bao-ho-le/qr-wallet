import { test, expect } from "@playwright/test";
import { createTestQR } from "./qr.seed";

/**
 * QR DETAIL LOAD
 */
test("QR detail page loads successfully", async ({ request, page }) => {
  const created = await createTestQR(request);

  await page.goto(`/qr/get?id=${created.id}`);

  const detail = page.getByTestId("qr-detail");

  await expect(detail).toBeVisible();
});

/**
 * QR DETAIL DATA CHECK
 */
test("QR detail displays correct data", async ({ request, page }) => {
  const created = await createTestQR(request);

  await page.goto(`/qr/get?id=${created.id}`);

  await page.waitForResponse(
    (res) => res.url().includes(`/qr/${created.id}`) && res.status() === 200,
  );

  const detail = page.getByTestId("qr-detail");

  await expect(detail).toContainText(created.name);
  await expect(detail).toContainText(created.bank);
  await expect(detail).toContainText(created.accountNo);
  await expect(detail).toContainText(created.note);
});

/**
 * EDIT NAVIGATION
 */
test("QR detail edit navigation works", async ({ request, page }) => {
  const created = await createTestQR(request);

  await page.goto(`/qr/get?id=${created.id}`);

  await page.getByTestId("edit-btn").click();

  await expect(page).toHaveURL(/qr\/update/);
});

/**
 * DELETE FLOW (IMPORTANT)
 */
test("QR detail show popup when delete", async ({ request, page }) => {
  const created = await createTestQR(request);

  await page.goto(`/qr/get?id=${created.id}`);

  page.once("dialog", async (dialog) => {
    expect(dialog.type()).toBe("confirm");
    await dialog.dismiss(); // hoặc accept
  });

  await page.getByTestId("delete-btn").click();
});
