package com.example.qr_wallet.qr.scanQR;

import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.exception.QRScanException;
import com.example.qr_wallet.qr.util.FileValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("QRService Integration Tests")
public class IntegrationTest {

    @Autowired
    private QRService qrService;

    @Test
    @DisplayName("Should validate file size correctly")
    public void testFileValidation_SizeLimit() {
        byte[] largeFileContent = new byte[11 * 1024 * 1024]; // 11MB

        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.png",
                MediaType.IMAGE_PNG_VALUE,
                largeFileContent
        );

        QRScanException exception = assertThrows(QRScanException.class, () -> {
            FileValidationUtil.validateQRImageFile(largeFile);
        });

        assertTrue(exception.getMessage().contains("exceeds maximum limit"));
    }

    @Test
    @DisplayName("Should reject non-image files")
    public void testFileValidation_InvalidMimeType() {
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "%PDF-1.4".getBytes()
        );

        QRScanException exception = assertThrows(QRScanException.class, () -> {
            FileValidationUtil.validateQRImageFile(pdfFile);
        });

        assertTrue(exception.getMessage().contains("Invalid file"));
    }

    @Test
    @DisplayName("Should accept valid PNG files")
    public void testFileValidation_ValidPNG() {
        byte[] pngBytes = {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };

        MockMultipartFile validPNG = new MockMultipartFile(
                "file",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                pngBytes
        );

        // Should not throw exception
        FileValidationUtil.validateQRImageFile(validPNG);
    }

    @Test
    @DisplayName("Should accept valid JPEG files")
    public void testFileValidation_ValidJPEG() {
        byte[] jpegBytes = {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
        };

        MockMultipartFile validJPEG = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                jpegBytes
        );

        // Should not throw exception
        FileValidationUtil.validateQRImageFile(validJPEG);
    }
}

