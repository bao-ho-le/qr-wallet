package com.example.qr_wallet.qr.getQRById;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void getQRById_whenQRExists_shouldReturnQR() throws Exception {
        QR savedQR = repo.save(QR.builder()
                .name("Le Vo")
                .bank("Vietcombank")
                .accountNo("123456789")
                .qrData("qr-data")
                .note("personal note")
                .build());

        mockMvc.perform(get("/api/v1/qr/{id}", savedQR.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedQR.getId()))
                .andExpect(jsonPath("$.name").value("Le Vo"))
                .andExpect(jsonPath("$.bank").value("Vietcombank"))
                .andExpect(jsonPath("$.accountNo").value("123456789"))
                .andExpect(jsonPath("$.qrData").value("qr-data"))
                .andExpect(jsonPath("$.note").value("personal note"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getQRById_whenQRNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/qr/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("QR not found"));
    }

    @Test
    void getQRById_whenIdIsInvalid_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/qr/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }
}