"use client";

import { useState } from "react";
import Link from "next/link";
import { API_BASE, readErrorResponse } from "@/lib/qr-api";

export default function DeleteQRPage() {
  const [qrId, setQrId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function handleDelete() {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const response = await fetch(`${API_BASE}/${qrId.trim()}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        setError(await readErrorResponse(response));
        return;
      }

      setSuccess("QR deleted successfully");
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
        <p>Delete a QR record by ID.</p>
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
        </div>

        <div className="actions">
          <button
            className="secondary"
            onClick={handleDelete}
            disabled={!qrId.trim() || loading}
          >
            {loading ? "Deleting..." : "Delete"}
          </button>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {success ? <div className="success">{success}</div> : null}
      </section>
    </main>
  );
}
