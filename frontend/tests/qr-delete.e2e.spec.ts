import { test, expect } from "@playwright/test";
import { createTestQR } from "./qr.seed";

test("Delete QR successfully", async ({ request, page }) => {
  // SEED DATA (tạo QR trước khi test)
  const created = await createTestQR(request);
  const id = created.id;

  await page.goto(`/qr/get?id=${id}`);

  page.on("dialog", (dialog) => dialog.accept());

  await page.getByTestId("delete-btn").click();

  await expect(page).toHaveURL(/qr\/list/);

  await expect(page.getByText("QR deleted successfully")).toBeVisible();
});
