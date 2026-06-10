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
        <p>
          Edit a QR record by ID and submit the updated data to the backend.
        </p>
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
                setForm((current) => ({ ...current, note: event.target.value }))
              }
              placeholder="Enter note"
            />
            {fieldErrors.note ? (
              <div className="field-error">{fieldErrors.note}</div>
            ) : null}
          </div>
        </div>

        <div className="actions">
          <button
            onClick={() => void handleUpdate()}
            disabled={!qrId.trim() || loading}
            className="button-base "
          >
            {loading ? "Updating..." : "Update"}
          </button>
        </div>

        {result ? <QRDetailCard detail={result} /> : null}
      </section>
    </main>
  );
}
