"use client";

import { useState } from "react";
import Link from "next/link";
import { QRDetailCard } from "@/components/qr-detail";
import { API_BASE, type QRDetail, readErrorResponse } from "@/lib/qr-api";

export default function GetQRPage() {
  const [qrId, setQrId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState<QRDetail | null>(null);

  async function handleFetch() {
    setLoading(true);
    setError("");
    setResult(null);

    try {
      const response = await fetch(`${API_BASE}/${qrId.trim()}`);

      if (!response.ok) {
        setError(await readErrorResponse(response));
        return;
      }

      const data = (await response.json()) as QRDetail;
      setResult(data);
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
        <h1>Get QR Detail</h1>
        <p>Fetch a QR record by ID and display its full details.</p>
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
          <button onClick={handleFetch} disabled={!qrId.trim() || loading}>
            {loading ? "Fetching..." : "Fetch"}
          </button>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {result ? <QRDetailCard detail={result} /> : null}
      </section>
    </main>
  );
}
