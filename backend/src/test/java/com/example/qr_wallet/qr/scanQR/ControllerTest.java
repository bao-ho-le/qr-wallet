package com.example.qr_wallet.qr.scanQR;

import com.example.qr_wallet.qr.QRController;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRScanRes;
import com.example.qr_wallet.qr.exception.QRScanException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QRController.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRService service;

    @Test
    @DisplayName("TC-CTRL-01 Upload QR Success")
    void uploadAndScanQR_whenValidQRCodeImage_shouldReturn200AndResponse()
            throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "qr.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
        );

        QRScanRes response = new QRScanRes(
                "RAW_QR_DATA",
                "970436",
                "Vietcombank",
                "123456789",
                "Nguyen Van A",
                100000L,
                "Thanh toan",
                false
        );

        when(service.uploadAndScanQR(any()))
                .thenReturn(response);

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawData")
                        .value("RAW_QR_DATA"))
                .andExpect(jsonPath("$.bankCode")
                        .value("970436"))
                .andExpect(jsonPath("$.bankName")
                        .value("Vietcombank"))
                .andExpect(jsonPath("$.accountNumber")
                        .value("123456789"))
                .andExpect(jsonPath("$.accountName")
                        .value("Nguyen Van A"))
                .andExpect(jsonPath("$.amount")
                        .value(100000))
                .andExpect(jsonPath("$.description")
                        .value("Thanh toan"))
                .andExpect(jsonPath("$.requireAccountName")
                        .value(false));

        verify(service, times(1))
                .uploadAndScanQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-02 Missing File")
    void uploadAndScanQR_whenFileMissing_shouldReturn400()
            throws Exception {

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                )
                .andExpect(status().isBadRequest());

        verify(service, never())
                .uploadAndScanQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-03 requireAccountName = true")
    void uploadAndScanQR_whenAccountNameMissing_shouldReturnRequireAccountNameTrue()
            throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "qr.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
        );

        QRScanRes response = new QRScanRes(
                "RAW_QR_DATA",
                "970436",
                "Vietcombank",
                "123456789",
                null,
                100000L,
                "Thanh toan",
                true
        );

        when(service.uploadAndScanQR(any()))
                .thenReturn(response);

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requireAccountName")
                        .value(true));

        verify(service, times(1))
                .uploadAndScanQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-04 Service Throws QRScanException")
    void uploadAndScanQR_whenServiceThrowsQRScanException_shouldReturn400()
            throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "qr.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-content".getBytes()
        );

        when(service.uploadAndScanQR(any()))
                .thenThrow(new QRScanException("Validation Error"));

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isBadRequest());


        verify(service, times(1))
                .uploadAndScanQR(any());
    }
}