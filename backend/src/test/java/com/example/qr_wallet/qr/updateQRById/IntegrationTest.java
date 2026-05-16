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
                .accountNo("111111111")
                .qrData("old-qr-data")
                .note("old note")
                .build());

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "Vietcombank",
                "123456789",
                "new-qr-data",
                "updated note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", savedQR.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedQR.getId()))
                .andExpect(jsonPath("$.name").value("Le Vo"))
                .andExpect(jsonPath("$.bank").value("Vietcombank"))
                .andExpect(jsonPath("$.accountNo").value("123456789"))
                .andExpect(jsonPath("$.qrData").value("new-qr-data"))
                .andExpect(jsonPath("$.note").value("updated note"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        QR updatedQR = repo.findById(savedQR.getId()).orElseThrow();
        Assertions.assertEquals("Le Vo", updatedQR.getName());
        Assertions.assertEquals("Vietcombank", updatedQR.getBank());
        Assertions.assertEquals("123456789", updatedQR.getAccountNo());
        Assertions.assertEquals("new-qr-data", updatedQR.getQrData());
        Assertions.assertEquals("updated note", updatedQR.getNote());
        Assertions.assertNotNull(updatedQR.getCreatedAt());
        Assertions.assertNotNull(updatedQR.getUpdatedAt());
    }

    @Test
    void updateQRById_whenQRNotFound_shouldReturn404() throws Exception {
        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "Vietcombank",
                "123456789",
                "qr-data",
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
                "Vietcombank",
                "123456789",
                "qr-data",
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
                .accountNo("111111111")
                .qrData("old-qr-data")
                .note("old note")
                .build());

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "Vietcombank",
                "123456789",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", savedQR.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"));

        QR unchangedQR = repo.findById(savedQR.getId()).orElseThrow();
        Assertions.assertEquals("Old Name", unchangedQR.getName());
        Assertions.assertEquals("Old Bank", unchangedQR.getBank());
        Assertions.assertEquals("111111111", unchangedQR.getAccountNo());
        Assertions.assertEquals("old-qr-data", unchangedQR.getQrData());
        Assertions.assertEquals("old note", unchangedQR.getNote());
    }

    @Test
    void updateQRById_whenMultipleFieldsAreBlank_shouldReturn400() throws Exception {
        QR savedQR = repo.save(QR.builder()
                .name("Old Name")
                .bank("Old Bank")
                .accountNo("111111111")
                .qrData("old-qr-data")
                .note("old note")
                .build());

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "",
                "",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", savedQR.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.bank").value("Bank is required"))
                .andExpect(jsonPath("$.accountNo").value("Account number is required"));

        QR unchangedQR = repo.findById(savedQR.getId()).orElseThrow();
        Assertions.assertEquals("Old Name", unchangedQR.getName());
        Assertions.assertEquals("Old Bank", unchangedQR.getBank());
        Assertions.assertEquals("111111111", unchangedQR.getAccountNo());
        Assertions.assertEquals("old-qr-data", unchangedQR.getQrData());
        Assertions.assertEquals("old note", unchangedQR.getNote());
    }
}