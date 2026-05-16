import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "QR Wallet",
  description: "Simple QR wallet frontend for backend operations",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
