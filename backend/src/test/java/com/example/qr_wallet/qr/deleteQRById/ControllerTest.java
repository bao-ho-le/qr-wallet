package com.example.qr_wallet.qr.deleteQRById;

import com.example.qr_wallet.qr.QRController;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.exception.GlobalExceptionHandler;
import com.example.qr_wallet.qr.exception.QRNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(QRController.class)
@Import(GlobalExceptionHandler.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRService service;

    @Test
    void deleteQRById_whenQRExists_shouldReturn200() throws Exception{

        mockMvc.perform(delete("/api/v1/qr/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("QR deleted successfully"));
    }

    @Test
    void deleteQRById_whenQRNotFound_shouldReturn404() throws Exception{

        doThrow(new QRNotFoundException("QR not found"))
            .when(service).deleteQRById(1L);

        mockMvc.perform(delete("/api/v1/qr/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("QR not found"));
    }

    @Test
    void deleteQRById_whenIdIsNegative_shouldReturn400() throws Exception {

        doThrow(new IllegalArgumentException("Invalid QR id"))
                .when(service).deleteQRById(-1L);

        mockMvc.perform(delete("/api/v1/qr/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }

    @Test
    void deleteQRById_whenIdIsZero_shouldReturn400() throws Exception {

        doThrow(new IllegalArgumentException("Invalid QR id"))
                .when(service).deleteQRById(0L);

        mockMvc.perform(delete("/api/v1/qr/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }

}



