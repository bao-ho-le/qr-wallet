import { test, expect } from "@playwright/test";
import { createTestQR } from "./qr.seed";

/**
 * LOAD EXISTING DATA
 */
test("Edit page loads and displays existing data", async ({
  request,
  page,
}) => {
  const created = await createTestQR(request);

  await page.goto(`/qr/update?id=${created.id}`);

  const nameInput = page.getByTestId("name-input");
  const noteInput = page.getByTestId("note-input");

  await expect(nameInput).toBeVisible();
  await expect(noteInput).toBeVisible();

  await expect(nameInput).toHaveValue(created.name);
  await expect(noteInput).toHaveValue(created.note);
});

/**
 * UPDATE QR SUCCESS
 */
test("Edit page updates QR successfully", async ({ request, page }) => {
  const created = await createTestQR(request);

  await page.goto(`/qr/update?id=${created.id}`);

  const nameInput = page.getByTestId("name-input");
  const noteInput = page.getByTestId("note-input");

  await nameInput.fill("Le Vo updated");
  await noteInput.fill("note updated");

  await page.getByTestId("update-btn").click();

  await expect(page.getByText("QR updated successfully")).toBeVisible();

  await expect(page.getByTestId("qr-name")).toContainText("Le Vo updated");
  await expect(page.getByTestId("qr-note")).toContainText("note updated");
});

/**
 * VALIDATION TEST
 */
test("Edit page shows validation when name is empty", async ({
  request,
  page,
}) => {
  const created = await createTestQR(request);

  await page.goto(`/qr/update?id=${created.id}`);

  const nameInput = page.getByTestId("name-input");

  await nameInput.fill("");

  await page.getByTestId("update-btn").click();

  await expect(page.getByText("Name is required")).toBeVisible();
});
