package com.example.qr_wallet.qr.updateQRById;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import com.example.qr_wallet.qr.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(GlobalExceptionHandler.class)
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QRRepo repo;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateQRById_whenValidRequest_shouldUpdateSuccessfully() throws Exception {

        QR savedQR = repo.save(QR.builder()
                .name("Old Name")
                .bank("Old Bank")
                .accountNo("1111111112")
                .qrData("old-qr-data")
                .note("old note")
                .build());

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "updated note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", savedQR.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedQR.getId()))
                .andExpect(jsonPath("$.name").value("Le Vo"))
                .andExpect(jsonPath("$.bank").value("Old Bank"))
                .andExpect(jsonPath("$.accountNo").value("1111111112"))
                .andExpect(jsonPath("$.qrData").value("old-qr-data"))
                .andExpect(jsonPath("$.note").value("updated note"));

        QR updatedQR = repo.findById(savedQR.getId()).orElseThrow();

        assertEquals("Le Vo", updatedQR.getName());
        assertEquals("Old Bank", updatedQR.getBank());
        assertEquals("1111111112", updatedQR.getAccountNo());
        assertEquals("old-qr-data", updatedQR.getQrData());
        assertEquals("updated note", updatedQR.getNote());
    }

    @Test
    void updateQRById_whenQRNotFound_shouldReturn404() throws Exception {
        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("QR not found"));
    }

    @Test
    void updateQRById_whenIdIsNegative_shouldReturn400() throws Exception {
        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }

    @Test
    void updateQRById_whenNameIsBlank_shouldReturn400() throws Exception {
        QR savedQR = repo.save(QR.builder()
                .name("Old Name")
                .bank("Old Bank")
                .accountNo("1111111113")
                .qrData("old-qr-data")
                .note("old note")
                .build());

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", savedQR.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"));

        QR unchangedQR = repo.findById(savedQR.getId()).orElseThrow();

        assertEquals("Old Name", unchangedQR.getName());
        assertEquals("Old Bank", unchangedQR.getBank());
        assertEquals("1111111113", unchangedQR.getAccountNo());
        assertEquals("old-qr-data", unchangedQR.getQrData());
        assertEquals("old note", unchangedQR.getNote());
    }

}