"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

import { useSearchParams } from "next/navigation";
import { QRDetailCard } from "@/components/qr-detail";
import {
  type QRDetail,
  type UpdateQRRequest,
  readApiError,
  readJsonResponse,
  requestQrDetail,
  requestUpdateQr,
} from "@/lib/qr-api";

export default function UpdateQRClient() {
  const [qrId, setQrId] = useState("");
  const [form, setForm] = useState<UpdateQRRequest>({
    name: "",
    bank: "",
    accountNo: "",
    qrData: "",
    note: "",
  });
  const [loading, setLoading] = useState(false);
  const [loadingRecord, setLoadingRecord] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [result, setResult] = useState<QRDetail | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const searchParams = useSearchParams();

  async function loadRecord(targetId = qrId) {
    const id = targetId.trim();

    if (!id) {
      setError("Please enter a QR ID.");
      return;
    }

    setLoadingRecord(true);
    setError("");
    setSuccess("");
    setFieldErrors({});

    try {
      const response = await requestQrDetail(id);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        return;
      }

      const data = await readJsonResponse<QRDetail>(response);
      setResult(data);
      setForm({
        name: data.name ?? "",
        bank: data.bank ?? "",
        accountNo: data.accountNo ?? "",
        qrData: data.qrData ?? "",
        note: data.note ?? "",
      });
    } catch {
      setError("Network error");
    } finally {
      setLoadingRecord(false);
    }
  }

  useEffect(() => {
    const id = searchParams.get("id")?.trim() ?? "";

    if (id && id !== qrId) {
      setQrId(id);
      void loadRecord(id);
    }
  }, [qrId, searchParams]);

  async function handleUpdate() {
    const nextFieldErrors: Record<string, string> = {};

    if (!qrId.trim()) {
      setError("Please enter a QR ID.");
      return;
    }

    if (!form.name.trim()) {
      nextFieldErrors.name = "Name is required.";
    }

    if (!form.bank.trim()) {
      nextFieldErrors.bank = "Bank is required.";
    }

    if (!form.accountNo.trim()) {
      nextFieldErrors.accountNo = "Account number is required.";
    }

    if (!form.qrData.trim()) {
      nextFieldErrors.qrData = "QR data is required.";
    }

    if (Object.keys(nextFieldErrors).length > 0) {
      setFieldErrors(nextFieldErrors);
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");
    setFieldErrors({});
    setResult(null);

    try {
      const response = await requestUpdateQr(qrId.trim(), form);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        setFieldErrors(apiError.fieldErrors);
        return;
      }

      const data = await readJsonResponse<QRDetail>(response);
      setResult(data);
      setSuccess("QR updated successfully");
    } catch {
      setError("Network error");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="feature-shell">
      <div className="feature-topbar">
        <Link className="back-link" href="/">
          Back to home
        </Link>
        <h1>Update QR</h1>
        <p>Edit a QR record by ID and submit the updated data to the backend.</p>
      </div>

      <section className="feature-card stack">
        

        <div className="actions ">
          <Link className="button-base secondary-link" href="/qr/list">
            Back to list
          </Link>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {success ? <div className="success">{success}</div> : null}

        <div className="field-group">
          <div className="field">
            <label htmlFor="name">Name</label>
            <input
              id="name"
              value={form.name}
              onChange={(event) =>
                setForm((current) => ({ ...current, name: event.target.value }))
              }
              placeholder="Enter name"
            />
            {fieldErrors.name ? <div className="field-error">{fieldErrors.name}</div> : null}
          </div>

          <div className="field">
            <label htmlFor="bank">Bank</label>
            <input
              id="bank"
              value={form.bank}
              onChange={(event) =>
                setForm((current) => ({ ...current, bank: event.target.value }))
              }
              placeholder="Enter bank"
            />
            {fieldErrors.bank ? <div className="field-error">{fieldErrors.bank}</div> : null}
          </div>

          <div className="field">
            <label htmlFor="accountNo">Account No</label>
            <input
              id="accountNo"
              value={form.accountNo}
              onChange={(event) =>
                setForm((current) => ({
                  ...current,
                  accountNo: event.target.value,
                }))
              }
              placeholder="Enter account number"
            />
            {fieldErrors.accountNo ? (
              <div className="field-error">{fieldErrors.accountNo}</div>
            ) : null}
          </div>

          <div className="field">
            <label htmlFor="qrData">QR Data</label>
            <textarea
              id="qrData"
              value={form.qrData}
              onChange={(event) =>
                setForm((current) => ({
                  ...current,
                  qrData: event.target.value,
                }))
              }
              placeholder="Enter QR data"
            />
            {fieldErrors.qrData ? <div className="field-error">{fieldErrors.qrData}</div> : null}
          </div>

          <div className="field">
            <label htmlFor="note">Note</label>
            <textarea
              id="note"
              value={form.note}
              onChange={(event) =>
                setForm((current) => ({ ...current, note: event.target.value }))
              }
              placeholder="Enter note"
            />
            {fieldErrors.note ? <div className="field-error">{fieldErrors.note}</div> : null}
          </div>
        </div>

        <div className="actions">
          <button onClick={() => void handleUpdate()} disabled={!qrId.trim() || loading} className="button-base ">
            {loading ? "Updating..." : "Update"}
          </button>
        </div>

        {result ? <QRDetailCard detail={result} /> : null}
      </section>
    </main>
  );
}