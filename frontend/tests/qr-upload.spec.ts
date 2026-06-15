import { test, expect } from "@playwright/test";

test.beforeEach(async ({ request }) => {
  await request.post("http://localhost:8080/api/test/cleanup");
});

test("TC3 - Upload page loads", async ({ page }) => {
  await page.goto("/qr/upload");

  await expect(page.getByText(/upload qr/i)).toBeVisible();
});

test("TC4 - File input exists", async ({ page }) => {
  await page.goto("/qr/upload");

  await expect(page.locator('input[type="file"]')).toBeVisible();
});

test("TC5 - Upload image shows scan button", async ({ page }) => {
  await page.goto("/qr/upload");

  const fileInput = page.locator('input[type="file"]');

  await fileInput.setInputFiles("tests/assets/qr.jpg");

  await expect(page.getByRole("button", { name: /scan/i })).toBeVisible();
});

test("TC6 - Scan QR success", async ({ page }) => {
  await page.goto("/qr/upload");

  await page.locator('input[type="file"]').setInputFiles("tests/assets/qr.jpg");

  await page.getByRole("button", { name: /scan/i }).click();

  // wait UI
  await expect(page.getByText(/parsed qr data/i)).toBeVisible();

  // backend data check (UI only)
  await expect(
    page.locator(".detail-row").filter({ hasText: "Bank" }),
  ).toContainText("MB Bank");

  await expect(
    page.locator(".detail-row").filter({ hasText: "Account No" }),
  ).toBeVisible();

  await expect(
    page.locator(".detail-row").filter({ hasText: "Account Name" }),
  ).toBeVisible();
});

test("TC7 - Save QR success", async ({ page }) => {
  const id = crypto.randomUUID();

  // MOCK SCAN API
  await page.route("**/api/v1/qr/scan", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        rawData: "mock",
        bankCode: "970436",
        bankName: "Vietcombank",
        accountNumber: `ACC-${id}`,
        accountName: "",
        amount: 0,
        description: "",
        requireAccountName: true,
      }),
    });
  });

  await page.goto("/qr/upload");

  await page.locator('input[type="file"]').setInputFiles("tests/assets/qr.jpg");

  await page.getByRole("button", { name: /scan/i }).click();

  await expect(page.getByText(/parsed qr data/i)).toBeVisible();

  await page.locator("#name").fill(`Test QR ${id}`);
  await page.locator("#note").fill(`Note ${id}`);

  await page.getByRole("button", { name: /save/i }).click();

  await expect(page.getByText(/created successfully/i)).toBeVisible();
});

test("TC8 - Name validation fail", async ({ page }) => {
  const id = crypto.randomUUID();

  await page.goto("/qr/upload");

  await page.locator('input[type="file"]').setInputFiles("tests/assets/qr.jpg");

  await page.getByRole("button", { name: /scan/i }).click();

  await expect(page.getByText(/parsed qr data/i)).toBeVisible();

  await page.locator("#name").waitFor();

  // optional fill first then clear (simulate user)
  await page.locator("#name").fill(`Temp ${id}`);
  await page.locator("#name").fill("");

  await page.getByRole("button", { name: /save/i }).click();

  await expect(page.getByText(/name is required/i)).toBeVisible();
});
