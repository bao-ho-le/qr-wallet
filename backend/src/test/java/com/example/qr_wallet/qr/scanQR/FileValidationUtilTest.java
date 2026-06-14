package com.example.qr_wallet.qr.scanQR;

import com.example.qr_wallet.qr.exception.QRScanException;
import com.example.qr_wallet.qr.util.FileValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidationUtilTest {

    @Test
    @DisplayName("TC-VAL-01 File Null")
    void validateQRImageFile_whenFileIsNull_shouldThrowQRScanException() {

        assertThatThrownBy(() ->
                FileValidationUtil.validateQRImageFile(null))
                .isInstanceOf(QRScanException.class)
                .hasMessage("File is required and cannot be empty");
    }

    @Test
    @DisplayName("TC-VAL-02 File Empty")
    void validateQRImageFile_whenFileIsEmpty_shouldThrowQRScanException() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "qr.png",
                "image/png",
                new byte[0]
        );

        assertThatThrownBy(() ->
                FileValidationUtil.validateQRImageFile(file))
                .isInstanceOf(QRScanException.class)
                .hasMessage("File is required and cannot be empty");
    }

    @Test
    @DisplayName("TC-VAL-03 File Size Exceeds 10MB")
    void validateQRImageFile_whenFileSizeExceeds10MB_shouldThrowQRScanException() {

        byte[] content = new byte[11 * 1024 * 1024];

        MultipartFile file = new MockMultipartFile(
                "file",
                "qr.png",
                "image/png",
                content
        );

        assertThatThrownBy(() ->
                FileValidationUtil.validateQRImageFile(file))
                .isInstanceOf(QRScanException.class)
                .hasMessage("File size exceeds maximum limit of 10MB");
    }

    @Test
    @DisplayName("TC-VAL-04 Invalid Extension TXT")
    void validateQRImageFile_whenExtensionIsTxt_shouldThrowQRScanException() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "qr.txt",
                "text/plain",
                "hello".getBytes()
        );

        assertThatThrownBy(() ->
                FileValidationUtil.validateQRImageFile(file))
                .isInstanceOf(QRScanException.class)
                .hasMessage("Invalid file format. Only PNG, JPG, JPEG are allowed");
    }

    @Test
    @DisplayName("TC-VAL-05 Missing Extension")
    void validateQRImageFile_whenExtensionMissing_shouldThrowQRScanException() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "qr",
                "image/png",
                "content".getBytes()
        );

        assertThatThrownBy(() ->
                FileValidationUtil.validateQRImageFile(file))
                .isInstanceOf(QRScanException.class)
                .hasMessage("Invalid file format. Only PNG, JPG, JPEG are allowed");
    }

    @Test
    @DisplayName("TC-VAL-06 Invalid Image Content")
    void validateQRImageFile_whenImageContentInvalid_shouldThrowQRScanException() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "qr.png",
                "image/png",
                "not-an-image".getBytes()
        );

        assertThatThrownBy(() ->
                FileValidationUtil.validateQRImageFile(file))
                .isInstanceOf(QRScanException.class)
                .hasMessage("Uploaded file is not a valid image");
    }

    @Test
    @DisplayName("TC-VAL-07 Valid PNG Image")
    void validateQRImageFile_whenValidPngImage_shouldReturnTrue() throws Exception {

        BufferedImage image =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        MultipartFile file = new MockMultipartFile(
                "file",
                "qr.png",
                "image/png",
                baos.toByteArray()
        );

        FileValidationUtil.validateQRImageFile(file);
    }
}