package com.example.qr_wallet.qr.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateQRRequest(

        @NotBlank(message = "Name is required")
        String name,

        String note
) {
}