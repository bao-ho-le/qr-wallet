package com.example.qr_wallet.qr.dto.response;

public record QRScanRes(
        String rawData,
        String bankCode,
        String bankName,
        String accountNumber,
        String accountName,
        Long amount,
        String description
) {
}
