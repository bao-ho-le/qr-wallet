"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import {
  type QRListItem,
  readApiError,
  readJsonResponse,
  requestQrList,
} from "@/lib/qr-api";

export default function QRListPage() {
  const [items, setItems] = useState<QRListItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const router = useRouter();
  const searchParams = useSearchParams();

  async function loadList() {
    setLoading(true);
    setError("");

    try {
      const response = await requestQrList();

      if (!response.ok) {
        const apiError = await readApiError(response);
        setError(apiError.message);
        return;
      }

      const data = await readJsonResponse<QRListItem[]>(response);
      setItems(Array.isArray(data) ? data : []);
    } catch {
      setError("Network error");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadList();
  }, []);

  useEffect(() => {
    const deleted = searchParams.get("deleted");

    if (deleted === "1") {
      setSuccess("QR deleted successfully");
      router.replace("/qr/list");
    }
  }, [router, searchParams]);

  return (
    <main className="feature-shell">
      <div className="feature-topbar">
        <Link className="back-link" href="/">
          Back to home
        </Link>
        <h1>QR List</h1>
        <p>
          Browse saved QR records and open any record for detail, edit, or
          delete.
        </p>
      </div>

      <section className="feature-card stack">
        <div className="actions">
          <button
            onClick={loadList}
            disabled={loading}
            className="button-base "
          >
            {loading ? "Refreshing..." : "Refresh"}
          </button>
          <Link className="button-base secondary-link" href="/qr/upload">
            Upload new QR
          </Link>
        </div>

        {error ? <div className="error">{error}</div> : null}
        {success ? <div className="success">{success}</div> : null}

        {!error && !loading && items.length === 0 ? (
          <div className="result">No QR records yet.</div>
        ) : null}

        <div className="record-list">
          {items.map((item) => (
            <article key={item.id} className="record-item">
              <div className="record-main">
                <div>
                  <h2>{item.name}</h2>
                  <div className="record-meta">
                    <span>Bank: {item.bank}</span>
                    <span>Account: {item.accountNo}</span>
                  </div>
                  <div className="record-note">Note: {item.note || "-"}</div>
                </div>
              </div>

              <div className="record-footer">
                <span className="meta">
                  Updated at: {item.updatedAt || "-"}
                </span>
                <div className="actions">
                  <Link
                    className="button-base secondary-link"
                    href={`/qr/get?id=${item.id}`}
                    prefetch={false}
                  >
                    Details
                  </Link>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}
