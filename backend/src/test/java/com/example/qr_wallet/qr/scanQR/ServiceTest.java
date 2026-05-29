package com.example.qr_wallet.qr.scanQR;

import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRScanRes;
import com.example.qr_wallet.qr.exception.QRScanException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QRService Scan Tests")
public class ServiceTest {

    private QRService qrService;

    @BeforeEach
    public void setUp() {
        qrService = new QRService(null);
    }

    @Test
    @DisplayName("Should throw exception when file is null")
    public void testUploadAndScanQR_WithNullFile() {
        assertThrows(QRScanException.class, () -> {
            qrService.uploadAndScanQR(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    public void testUploadAndScanQR_WithEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );

        QRScanException exception = assertThrows(QRScanException.class, () -> {
            qrService.uploadAndScanQR(emptyFile);
        });

        assertTrue(exception.getMessage().contains("empty"));
    }

    @Test
    @DisplayName("Should throw exception for invalid file format (txt)")
    public void testUploadAndScanQR_WithInvalidFileFormat() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is text".getBytes()
        );

        QRScanException exception = assertThrows(QRScanException.class, () -> {
            qrService.uploadAndScanQR(invalidFile);
        });

        assertTrue(exception.getMessage().contains("Invalid file format"));
    }

    @Test
    @DisplayName("Should throw exception for JPEG file with wrong MIME type")
    public void testUploadAndScanQR_WithWrongMimeType() {
        MockMultipartFile wrongMimeFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.TEXT_PLAIN_VALUE, // Wrong MIME type
                "fake image data".getBytes()
        );

        QRScanException exception = assertThrows(QRScanException.class, () -> {
            qrService.uploadAndScanQR(wrongMimeFile);
        });

        assertTrue(exception.getMessage().contains("Invalid file type"));
    }
}

