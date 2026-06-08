"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { QRDetailCard } from "@/components/qr-detail";
import {
  type QRDetail,
  readApiError,
  readJsonResponse,
  requestDeleteQr,
  requestQrDetail,
} from "@/lib/qr-api";

export default function DeleteQRClient() {
  const [qrId, setQrId] = useState("");
  const [loading, setLoading] = useState(false);
  const [loadingRecord, setLoadingRecord] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [result, setResult] = useState<QRDetail | null>(null);
  const router = useRouter();
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

    try {
      const response = await requestQrDetail(id);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        return;
      }

      const data = await readJsonResponse<QRDetail>(response);
      setResult(data);
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

  async function handleDelete() {
    const id = qrId.trim();

    if (!id) {
      setError("Please enter a QR ID.");
      return;
    }

    const confirmed = window.confirm(
      `Delete QR record ${id}? This cannot be undone.`,
    );

    if (!confirmed) {
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const response = await requestDeleteQr(id);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        return;
      }

      setSuccess("QR deleted successfully");
      setResult(null);
      router.push("/qr/list");
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
        <h1>Delete QR</h1>
        <p>Load a QR record first, then confirm deletion.</p>
      </div>

      <section className="feature-card stack">
        <div className="field-group">
          <div className="field">
            <label htmlFor="qrId">QR ID</label>
            <input
              id="qrId"
              type="number"
              min="1"
              value={qrId}
              onChange={(event) => setQrId(event.target.value)}
              placeholder="Enter QR id"
            />
          </div>
        </div>

        <div className="actions">
          <button onClick={() => void loadRecord()} disabled={!qrId.trim() || loadingRecord}>
            {loadingRecord ? "Loading..." : "Load data"}
          </button>
          <button
            className="secondary"
            onClick={() => void handleDelete()}
            disabled={!qrId.trim() || loading}
          >
            {loading ? "Deleting..." : "Delete"}
          </button>
          <Link className="secondary-link" href="/qr/list">
            Back to list
          </Link>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {success ? <div className="success">{success}</div> : null}
        {result ? <QRDetailCard detail={result} /> : null}
      </section>
    </main>
  );
}