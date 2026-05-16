package com.example.qr_wallet.qr.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateQRRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Bank is required")
        String bank,

        @NotBlank(message = "Account number is required")
        String accountNo,

        String qrData,

        String note
) {
}