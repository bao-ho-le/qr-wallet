"use client";

import { useState } from "react";
import Link from "next/link";
import { QRDetailCard } from "@/components/qr-detail";
import {
  type CreateQRRequest,
  type QRDetail,
  type QRScanResponse,
  readApiError,
  readMaybeJsonResponse,
  requestCreateQr,
  requestQrScan,
} from "@/lib/qr-api";

type UploadFormState = {
  name: string;
  note: string;
};

export default function UploadQRPage() {
  const [file, setFile] = useState<File | null>(null);
  const [scanResult, setScanResult] = useState<QRScanResponse | null>(null);
  const [createdDetail, setCreatedDetail] = useState<QRDetail | null>(null);
  const [form, setForm] = useState<UploadFormState>({
    name: "",
    note: "",
  });
  const [loadingScan, setLoadingScan] = useState(false);
  const [loadingCreate, setLoadingCreate] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const requireAccountName = scanResult?.requireAccountName ?? false;

  async function handleScan() {
    if (!file) {
      setError("Please choose a QR image first.");
      return;
    }

    setLoadingScan(true);
    setError("");
    setSuccess("");
    setFieldErrors({});
    setScanResult(null);
    setCreatedDetail(null);

    try {
      const response = await requestQrScan(file);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        setFieldErrors(apiError.fieldErrors);
        return;
      }

      const data = await readMaybeJsonResponse<QRScanResponse>(response);

      if (!data) {
        setError("Scan succeeded but the backend returned no QR data.");
        return;
      }

      setScanResult(data);
      setForm({
        name: data.requireAccountName ? "" : data.accountName || data.bankName || "",
        note: "",
      });
    } catch {
      setError("Network error");
    } finally {
      setLoadingScan(false);
    }
  }

  async function handleCreate() {
    if (!scanResult) {
      setError("Scan the QR first.");
      return;
    }

    const nextFieldErrors: Record<string, string> = {};
    const name = form.name.trim();

    if (requireAccountName && !name) {
      nextFieldErrors.name = "Name is required.";
    }

    if (Object.keys(nextFieldErrors).length > 0) {
      setFieldErrors(nextFieldErrors);
      return;
    }

    const payload: CreateQRRequest = {
      name: name || scanResult.accountName || scanResult.bankName,
      bank: scanResult.bankName || scanResult.bankCode,
      accountNo: scanResult.accountNumber,
      qrData: scanResult.rawData,
      note: form.note.trim(),
    };

    setLoadingCreate(true);
    setError("");
    setSuccess("");
    setFieldErrors({});

    try {
      const response = await requestCreateQr(payload);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        setFieldErrors(apiError.fieldErrors);
        return;
      }

      const created = await readMaybeJsonResponse<QRDetail>(response);

      if (created) {
        setCreatedDetail(created);
      } else {
        setCreatedDetail(null);
      }

      setSuccess("QR created successfully.");
    } catch {
      setError("Network error");
    } finally {
      setLoadingCreate(false);
    }
  }

  return (
    <main className="feature-shell">
      <div className="feature-topbar">
        <Link className="back-link" href="/">
          Back to home
        </Link>
        <h1>Upload QR</h1>
        <p>Upload a QR image, scan VietQR data, then save the record.</p>
      </div>

      <section className="feature-card stack">
        <div className="field-group">
          <div className="field">
            <label htmlFor="qrFile">QR image</label>
            <input
              id="qrFile"
              type="file"
              accept="image/*"
              onChange={(event) => {
                setFile(event.target.files?.[0] ?? null);
                setError("");
                setSuccess("");
                setFieldErrors({});
              }}
            />
            {file ? <div className="meta">Selected file: {file.name}</div> : null}
            {fieldErrors.file ? <div className="field-error">{fieldErrors.file}</div> : null}
          </div>
        </div>

        <div className="actions">
          <button onClick={handleScan} disabled={!file || loadingScan}>
            {loadingScan ? "Scanning..." : "Scan QR"}
          </button>
          <Link className="secondary-link" href="/qr/list">
            Go to list
          </Link>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {success ? <div className="success">{success}</div> : null}

        {scanResult ? (
          <div className="upload-preview">
            <h2>Parsed QR Data</h2>
            <div className="detail-list">
              <div className="detail-row">
                <span className="detail-label">Bank</span>
                <span>{scanResult.bankName || scanResult.bankCode}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Account No</span>
                <span>{scanResult.accountNumber}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Account Name</span>
                <span>{scanResult.accountName || "-"}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Amount</span>
                <span>{scanResult.amount ?? "-"}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Description</span>
                <span>{scanResult.description ?? "-"}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Require name</span>
                <span>{scanResult.requireAccountName ? "Yes" : "No"}</span>
              </div>
            </div>

            <div className="field-group">
              <div className="field">
                <label htmlFor="name">Name</label>
                <input
                  id="name"
                  value={form.name}
                  onChange={(event) =>
                    setForm((current) => ({
                      ...current,
                      name: event.target.value,
                    }))
                  }
                  placeholder={
                    requireAccountName
                      ? "Enter QR name"
                      : "Suggested from account name"
                  }
                />
                {fieldErrors.name ? (
                  <div className="field-error">{fieldErrors.name}</div>
                ) : null}
              </div>

              <div className="field">
                <label htmlFor="note">Note</label>
                <textarea
                  id="note"
                  value={form.note}
                  onChange={(event) =>
                    setForm((current) => ({
                      ...current,
                      note: event.target.value,
                    }))
                  }
                  placeholder="Enter note"
                />
                {fieldErrors.note ? (
                  <div className="field-error">{fieldErrors.note}</div>
                ) : null}
              </div>
            </div>

            <div className="actions">
              <button onClick={handleCreate} disabled={loadingCreate}>
                {loadingCreate ? "Saving..." : "Save QR"}
              </button>
            </div>
          </div>
        ) : null}

        {createdDetail ? <QRDetailCard detail={createdDetail} showQrData={false} /> : null}
      </section>
    </main>
  );
}