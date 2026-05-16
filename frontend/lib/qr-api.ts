export type QRDetail = {
  id: number;
  name: string;
  bank: string;
  accountNo: string;
  qrData?: string | null;
  note?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type UpdateQRRequest = {
  name: string;
  bank: string;
  accountNo: string;
  qrData: string;
  note: string;
};

export const API_BASE = "http://localhost:8080/api/v1/qr";

export function formatError(payload: unknown): string {
  if (typeof payload === "string") {
    return payload;
  }

  if (payload && typeof payload === "object") {
    const entries = Object.entries(payload as Record<string, unknown>);
    if (entries.length > 0) {
      return entries
        .map(([key, value]) => `${key}: ${String(value)}`)
        .join("\n");
    }
  }

  return "Unexpected error";
}

export async function readErrorResponse(response: Response): Promise<string> {
  const text = await response.text();

  if (!text) {
    return `Request failed with status ${response.status}`;
  }

  try {
    const json = JSON.parse(text) as unknown;
    return formatError(json);
  } catch {
    return text;
  }
}
