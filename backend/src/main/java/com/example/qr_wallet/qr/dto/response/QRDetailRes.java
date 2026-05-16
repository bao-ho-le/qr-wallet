package com.example.qr_wallet.qr.dto.response;

import java.time.LocalDateTime;

public record QRDetailRes(
        Long id,
        String name,
        String bank,
        String accountNo,
        String qrData,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
