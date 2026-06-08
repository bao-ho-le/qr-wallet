package com.example.qr_wallet.qr.exception;

public class QRAlreadyExistsException extends RuntimeException {
    public QRAlreadyExistsException(String message) {
        super(message);
    }
}
