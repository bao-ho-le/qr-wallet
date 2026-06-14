package com.example.qr_wallet.qr.getAllQRs;

import com.example.qr_wallet.qr.QRController;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRListItemRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QRController.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRService qrService;

    @Test
    @DisplayName("TC-CTRL-01 Get All QR Success")
    void getAllQRs_whenQRsExist_shouldReturn200AndList()
            throws Exception {

        LocalDateTime now = LocalDateTime.now();

        List<QRListItemRes> response = List.of(
                new QRListItemRes(
                        1L,
                        "Nguyen Van A",
                        "VCB",
                        "123456789",
                        "Note 1",
                        now
                ),
                new QRListItemRes(
                        2L,
                        "Tran Van B",
                        "MB",
                        "987654321",
                        "Note 2",
                        now
                )
        );

        when(qrService.getAllQRs())
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/qr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].id")
                        .value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Nguyen Van A"))
                .andExpect(jsonPath("$[0].bank")
                        .value("VCB"))
                .andExpect(jsonPath("$[0].accountNo")
                        .value("123456789"))
                .andExpect(jsonPath("$[0].note")
                        .value("Note 1"))
                .andExpect(jsonPath("$[1].id")
                        .value(2))
                .andExpect(jsonPath("$[1].name")
                        .value("Tran Van B"));

        verify(qrService, times(1))
                .getAllQRs();
    }

    @Test
    @DisplayName("TC-CTRL-02 Get All QR Empty List")
    void getAllQRs_whenNoQRExists_shouldReturn200AndEmptyList()
            throws Exception {

        when(qrService.getAllQRs())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/qr"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(qrService, times(1))
                .getAllQRs();
    }
}