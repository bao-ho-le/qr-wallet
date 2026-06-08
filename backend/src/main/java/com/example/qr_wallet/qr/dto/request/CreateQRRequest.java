package com.example.qr_wallet.qr.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateQRRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "bank is required")
        String bank,
        @NotBlank(message = "accountNo is required")
        String accountNo,
        @NotBlank(message = "qrData is required")
        String qrData,
        String note
) {
}
