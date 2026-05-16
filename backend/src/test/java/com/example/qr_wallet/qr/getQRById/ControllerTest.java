package com.example.qr_wallet.qr.getQRById;

import com.example.qr_wallet.qr.QRController;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.example.qr_wallet.qr.exception.GlobalExceptionHandler;
import com.example.qr_wallet.qr.exception.QRNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QRController.class)
@Import(GlobalExceptionHandler.class)
public class ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRService service;

    @Test
    void getQRById_whenQRFound_shouldReturn200AndQr() throws Exception {

        QRDetailRes response = new QRDetailRes(
            1L,
            "Le Vo",
            "Momo",
            "123456",
            "qr-data",
            "note",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(service.getQRById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/qr/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Le Vo"))
            .andExpect(jsonPath("$.bank").value("Momo"))
            .andExpect(jsonPath("$.accountNo").value("123456"))
            .andExpect(jsonPath("$.qrData").value("qr-data"))
            .andExpect(jsonPath("$.note").value("note"));
    }

    @Test
    void getQRById_whenQRNotFound_shouldReturn404() throws Exception {

        when(service.getQRById(1L))
                .thenThrow(new QRNotFoundException("QR not found"));

        mockMvc.perform(get("/api/v1/qr/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("QR not found"));
    }

    @Test
    void getQRById_whenIdIsNegative_shouldReturn400() throws Exception {

        when(service.getQRById(-1L))
               .thenThrow(new IllegalArgumentException("Invalid QR id"));

        mockMvc.perform(get("/api/v1/qr/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }

    @Test
    void getQRById_whenIdIsZero_shouldReturn400() throws Exception {

        when(service.getQRById(0L))
                .thenThrow(new IllegalArgumentException("Invalid QR id"));

        mockMvc.perform(get("/api/v1/qr/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }

}
