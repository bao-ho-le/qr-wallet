import { APIRequestContext } from "@playwright/test";

export async function createTestQR(request: APIRequestContext) {
  const random = Date.now();

  const payload = {
    name: "Le Vo",
    bank: "MB Bank",
    accountNo: `ACC-${random}`,
    qrData: "sample-qr-data",
    note: "note",
  };

  const response = await request.post("http://localhost:8080/api/v1/qr", {
    headers: { "Content-Type": "application/json" },
    data: payload,
  });

  if (!response.ok()) {
    console.log(await response.text());
    throw new Error("Failed to create test QR");
  }

  return await response.json();
}
