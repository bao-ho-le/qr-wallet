package com.example.qr_wallet.qr.exception;

public class QRScanException extends RuntimeException {
    public QRScanException(String message) {
        super(message);
    }

    public QRScanException(String message, Throwable cause) {
        super(message, cause);
    }
}

