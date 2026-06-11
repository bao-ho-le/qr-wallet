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

export default function GetQRClient() {
  const [qrId, setQrId] = useState("");
  const [loading, setLoading] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState<QRDetail | null>(null);
  const [notFound, setNotFound] = useState(false);
  const router = useRouter();
  const searchParams = useSearchParams();

  async function handleFetch(targetId: string) {
    const id = targetId.trim();

    if (!id) {
      setError("Missing QR ID in URL");
      return;
    }

    setLoading(true);
    setError("");
    setNotFound(false);
    setResult(null);

    try {
      const response = await requestQrDetail(id);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        setNotFound(response.status === 404);
        return;
      }

      const data = await readJsonResponse<QRDetail>(response);
      setResult(data);
    } catch {
      setError("Network error");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    const id = searchParams.get("id")?.trim() ?? "";

    if (id) {
      setQrId(id);
      void handleFetch(id);
    }
    // we only want to react when searchParams change
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams]);

  async function handleDelete() {
    const id = qrId.trim();

    if (!id) {
      setError("Missing QR ID in URL");
      return;
    }

    const confirmed = window.confirm(
      `Delete QR record ${id}? This cannot be undone.`,
    );

    if (!confirmed) {
      return;
    }

    setDeleting(true);
    setError("");

    try {
      const response = await requestDeleteQr(id);

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        return;
      }

      router.replace("/qr/list?deleted=1");
    } catch {
      setError("Network error");
    } finally {
      setDeleting(false);
    }
  }

  return (
    <main className="feature-shell" data-testid="qr-detail">
      <div className="feature-topbar">
        <Link className="back-link" href="/">
          Back to home
        </Link>
        {result ? (
          <>
            <h1>{result.name}</h1>
            <p className="meta">QR details</p>
          </>
        ) : (
          <>
            <h1>Get QR Detail</h1>
            <p>Fetch a QR record by ID and display its full details.</p>
          </>
        )}
      </div>

      <section className="feature-card stack">
        <div className="actions">
          <Link className="button-base secondary-link" href="/qr/list">
            Back to list
          </Link>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {notFound ? <div className="result">QR record not found.</div> : null}

        {result ? (
          <>
            <QRDetailCard detail={result} />
            <div className="actions detail-actions">
              <Link
                className="button-base secondary-link"
                href={`/qr/update?id=${result.id}`}
                prefetch={false}
                data-testid="edit-btn"
              >
                Edit
              </Link>
              <button
                type="button"
                className="button-base secondary-link danger-link"
                onClick={() => void handleDelete()}
                disabled={deleting}
                data-testid="delete-btn"
              >
                {deleting ? "Deleting..." : "Delete"}
              </button>
            </div>
          </>
        ) : null}
      </section>
    </main>
  );
}
