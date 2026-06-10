package com.example.qr_wallet.qr.exception;

public class QRConflictException extends RuntimeException {

    public QRConflictException(String message) {
        super(message);
    }

    public QRConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}