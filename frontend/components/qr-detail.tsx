import type { QRDetail } from "@/lib/qr-api";

export function QRDetailCard({ detail }: { detail: QRDetail }) {
  return (
    <div className="result">
      <div className="detail-list">
        <div className="detail-row">
          <span className="detail-label">ID</span>
          <span>{detail.id}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Name</span>
          <span>{detail.name}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Bank</span>
          <span>{detail.bank}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Account No</span>
          <span>{detail.accountNo}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">QR Data</span>
          <span>{detail.qrData ?? "-"}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Note</span>
          <span>{detail.note ?? "-"}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Created At</span>
          <span>{detail.createdAt ?? "-"}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Updated At</span>
          <span>{detail.updatedAt ?? "-"}</span>
        </div>
      </div>
    </div>
  );
}
