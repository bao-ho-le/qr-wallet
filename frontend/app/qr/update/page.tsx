import { Suspense } from "react";
import UpdateQRClient from "./update-client";

export default function UpdateQRPage() {
  return (
    <Suspense fallback={<main className="feature-shell">Loading QR update...</main>}>
      <UpdateQRClient />
    </Suspense>
  );
}