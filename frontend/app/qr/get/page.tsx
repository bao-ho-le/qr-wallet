import { Suspense } from "react";
import GetQRClient from "./get-client";

export default function GetQRPage() {
  return (
    <Suspense fallback={<main className="feature-shell">Loading QR detail...</main>}>
      <GetQRClient />
    </Suspense>
  );
}