package com.example.qr_wallet.qr.deleteQRById;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    void deleteQRById_whenQRExists_shouldDeleteQR() throws Exception {
        QR savedQR = repo.save(QR.builder()
                .name("Le Vo")
                .bank("Vietcombank")
                .accountNo("123456789")
                .qrData("qr-data")
                .note("note")
                .build());

        mockMvc.perform(delete("/api/v1/qr/{id}", savedQR.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("QR deleted successfully"));

        Assertions.assertFalse(repo.existsById(savedQR.getId()));
    }

    @Test
    void deleteQRById_whenQRNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/v1/qr/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("QR not found"));
    }

    @Test
    void deleteQRById_whenIdIsInvalid_shouldReturn400() throws Exception {
        mockMvc.perform(delete("/api/v1/qr/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }
}