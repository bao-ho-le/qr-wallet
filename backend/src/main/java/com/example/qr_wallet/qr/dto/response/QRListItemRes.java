package com.example.qr_wallet.qr.dto.response;

import java.time.LocalDateTime;

/**
 * DTO for listing QR items in a summarized form.
 */
public record QRListItemRes(
        Long id,
        String name,
        String bank,
        String accountNo,
        String note,
        LocalDateTime updatedAt
) {
}

