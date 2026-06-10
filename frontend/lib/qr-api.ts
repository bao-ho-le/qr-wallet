export type QRListItem = {
  id: number;
  name: string;
  bank: string;
  accountNo: string;
  note?: string | null;
  updatedAt?: string | null;
};

export type QRDetail = QRListItem & {
  qrData?: string | null;
  createdAt?: string | null;
};

export type QRScanResponse = {
  rawData: string;
  bankCode: string;
  bankName: string;
  accountNumber: string;
  accountName: string;
  amount: string | number | null;
  description: string | null;
  requireAccountName: boolean;
};

export type CreateQRRequest = {
  name: string;
  note: string;
};

export type UpdateQRRequest = CreateQRRequest;

export type ApiFieldErrors = Record<string, string>;

export type ApiErrorDetails = {
  message: string;
  fieldErrors: ApiFieldErrors;
};

export const API_BASE =
  process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080/api/v1/qr";

function buildUrl(path = ""): string {
  return `${API_BASE}${path}`;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return Boolean(value) && typeof value === "object" && !Array.isArray(value);
}

function isFieldErrorMap(value: unknown): value is ApiFieldErrors {
  if (!isRecord(value)) {
    return false;
  }

  return Object.values(value).every((entry) => typeof entry === "string");
}

export function formatError(payload: unknown): string {
  if (typeof payload === "string") {
    return payload;
  }

  if (Array.isArray(payload)) {
    return payload.map((entry) => String(entry)).join("\n");
  }

  if (isFieldErrorMap(payload)) {
    return Object.entries(payload)
      .map(([key, value]) => `${key}: ${value}`)
      .join("\n");
  }

  if (isRecord(payload)) {
    const entries = Object.entries(payload);
    if (entries.length > 0) {
      return entries
        .map(([key, value]) => `${key}: ${String(value)}`)
        .join("\n");
    }
  }

  return "Unexpected error";
}
function parseErrorPayload(payload: unknown, status: number): ApiErrorDetails {
  // ưu tiên message trước
  if (isRecord(payload) && typeof payload.message === "string") {
    const fieldErrors = isFieldErrorMap(payload.errors) ? payload.errors : {};

    return {
      message: payload.message,
      fieldErrors,
    };
  }

  // validation errors
  if (isFieldErrorMap(payload)) {
    return {
      message: "Please fix the highlighted fields.",
      fieldErrors: payload,
    };
  }

  // plain text response
  if (typeof payload === "string") {
    return {
      message: payload,
      fieldErrors: {},
    };
  }

  return {
    message: `Request failed with status ${status}`,
    fieldErrors: {},
  };
}

export async function readErrorResponse(response: Response): Promise<string> {
  const error = await readApiError(response);
  return error.message;
}

export async function readApiError(
  response: Response,
): Promise<ApiErrorDetails> {
  const text = await response.text();

  if (!text) {
    return {
      message: `Request failed with status ${response.status}`,
      fieldErrors: {},
    };
  }

  try {
    const json = JSON.parse(text) as unknown;
    return parseErrorPayload(json, response.status);
  } catch {
    return parseErrorPayload(text, response.status);
  }
}

export async function readJsonResponse<T>(response: Response): Promise<T> {
  const text = await response.text();

  if (!text) {
    throw new Error("Empty response body");
  }

  return JSON.parse(text) as T;
}

export async function readMaybeJsonResponse<T>(
  response: Response,
): Promise<T | null> {
  const text = await response.text();

  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text) as T;
  } catch {
    return null;
  }
}

export function requestQrList(): Promise<Response> {
  return fetch(buildUrl());
}

export function requestQrDetail(id: number | string): Promise<Response> {
  return fetch(buildUrl(`/${String(id).trim()}`));
}

export function requestQrScan(file: File): Promise<Response> {
  const formData = new FormData();
  formData.append("file", file);

  return fetch(buildUrl("/scan"), {
    method: "POST",
    body: formData,
  });
}

export function requestCreateQr(payload: CreateQRRequest): Promise<Response> {
  return fetch(buildUrl(), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });
}

export function requestUpdateQr(
  id: number | string,
  payload: UpdateQRRequest,
): Promise<Response> {
  return fetch(buildUrl(`/${String(id).trim()}`), {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });
}

export function requestDeleteQr(id: number | string): Promise<Response> {
  return fetch(buildUrl(`/${String(id).trim()}`), {
    method: "DELETE",
  });
}
