import Link from "next/link";

export default function Home() {
  return (
    <main className="landing-shell">
      <section className="hero">
        <p className="eyebrow">QR Wallet Demo</p>
        <h1>QR Wallet Management System</h1>
        <p className="hero-copy">
          A small Spring Boot and Next.js project for viewing, updating, and
          deleting QR records through a simple, student-friendly interface.
        </p>
      </section>

      <section className="nav-grid">
        <Link className="nav-card" href="/qr/get">
          <span className="nav-title">Get QR Detail</span>
          <span className="nav-text">
            Search a QR record by ID and view full details.
          </span>
        </Link>

        <Link className="nav-card" href="/qr/update">
          <span className="nav-title">Update QR</span>
          <span className="nav-text">
            Edit QR information and submit updates to the backend.
          </span>
        </Link>

        <Link className="nav-card" href="/qr/delete">
          <span className="nav-title">Delete QR</span>
          <span className="nav-text">
            Remove a QR record by ID with a single request.
          </span>
        </Link>
      </section>
    </main>
  );
}
