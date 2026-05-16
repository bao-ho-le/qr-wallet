"use client";

import { useState } from "react";
import Link from "next/link";
import { QRDetailCard } from "@/components/qr-detail";
import {
  API_BASE,
  type QRDetail,
  type UpdateQRRequest,
  readErrorResponse,
} from "@/lib/qr-api";

export default function UpdateQRPage() {
  const [qrId, setQrId] = useState("");
  const [form, setForm] = useState<UpdateQRRequest>({
    name: "",
    bank: "",
    accountNo: "",
    qrData: "",
    note: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [result, setResult] = useState<QRDetail | null>(null);

  async function handleUpdate() {
    setLoading(true);
    setError("");
    setSuccess("");
    setResult(null);

    try {
      const response = await fetch(`${API_BASE}/${qrId.trim()}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      });

      if (!response.ok) {
        setError(await readErrorResponse(response));
        return;
      }

      const data = (await response.json()) as QRDetail;
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

      <section className="feature-card">
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
          </div>
        </div>

        <div className="actions">
          <button onClick={handleUpdate} disabled={!qrId.trim() || loading}>
            {loading ? "Updating..." : "Update"}
          </button>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {success ? <div className="success">{success}</div> : null}
        {result ? <QRDetailCard detail={result} /> : null}
      </section>
    </main>
  );
}
