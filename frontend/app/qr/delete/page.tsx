import { Suspense } from "react";
import DeleteQRClient from "./delete-client";

export default function DeleteQRPage() {
  return (
    <Suspense fallback={<main className="feature-shell">Loading QR delete...</main>}>
      <DeleteQRClient />
    </Suspense>
  );
}