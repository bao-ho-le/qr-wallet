package com.example.qr_wallet.qr.scanQR;

import com.example.qr_wallet.qr.QRController;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRScanRes;
import com.example.qr_wallet.qr.exception.QRScanException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("QRController Scan Tests")
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRService qrService;

    @Test
    @DisplayName("Should return scanned QR data successfully")
    public void testUploadAndScanQR_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "qr.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake png data".getBytes()
        );

        QRScanRes expectedResponse = new QRScanRes("https://example.com/payment?id=123");
        when(qrService.uploadAndScanQR(mockFile)).thenReturn(expectedResponse);

        mockMvc.perform(
                multipart("/api/v1/qr/scan")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qrData").value("https://example.com/payment?id=123"));

        verify(qrService, times(1)).uploadAndScanQR(any());
    }

    @Test
    @DisplayName("Should return 400 when file validation fails")
    public void testUploadAndScanQR_ValidationFailed() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "invalid.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "not an image".getBytes()
        );

        when(qrService.uploadAndScanQR(invalidFile))
                .thenThrow(new QRScanException("Invalid file format"));

        mockMvc.perform(
                multipart("/api/v1/qr/scan")
                        .file(invalidFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid file format"));
    }

    @Test
    @DisplayName("Should return 400 when QR code cannot be decoded")
    public void testUploadAndScanQR_DecodingFailed() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "no_qr.png",
                MediaType.IMAGE_PNG_VALUE,
                "image without qr code".getBytes()
        );

        when(qrService.uploadAndScanQR(mockFile))
                .thenThrow(new QRScanException("No QR code found in the image"));

        mockMvc.perform(
                multipart("/api/v1/qr/scan")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No QR code found in the image"));
    }
}

