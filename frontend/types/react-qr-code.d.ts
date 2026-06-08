declare module "react-qr-code" {
  import { ComponentType } from "react";

  export interface QRCodeProps {
    value: string;
    size?: number;
    bgColor?: string;
    fgColor?: string;
    level?: "L" | "M" | "Q" | "H";
  }

  const QRCode: ComponentType<QRCodeProps>;

  export default QRCode;
}