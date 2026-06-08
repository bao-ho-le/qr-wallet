import Link from "next/link";

export default function Home() {
  return (
    <main className="landing-shell flex flex-col min-h-screen items-center">
      <section className="hero">
        <p className="eyebrow">QR Wallet Demo</p>
        <h1>QR Wallet Management System</h1>
        <p className="hero-copy">
          Upload a VietQR image, scan it into structured data, and manage saved
          QR records through a simple, student-friendly interface.
        </p>
      </section>

      <section className="nav-grid flex-grow flex items-center justify-center">
        <Link className="nav-card" href="/qr/upload">
          <span className="nav-title">Upload QR</span>
          <span className="nav-text">
            Upload an image, scan VietQR data, and save the record after review.
          </span>
        </Link>

        <Link className="nav-card" href="/qr/list">
          <span className="nav-title">QR List</span>
          <span className="nav-text">
            Browse saved QR records, open detail, edit, or delete entries.
          </span>
        </Link>
      </section>
    </main>
  );
}
