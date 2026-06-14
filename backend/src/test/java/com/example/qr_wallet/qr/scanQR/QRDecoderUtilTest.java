package com.example.qr_wallet.qr.scanQR;

import com.example.qr_wallet.qr.exception.QRScanException;
import com.example.qr_wallet.qr.util.QRDecoderUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QRDecoderUtilTest {

    @Test
    @DisplayName("TC-DEC-01 Decode Success")
    void decodeQRFromImage_whenValidQR_shouldReturnDecodedText()
            throws Exception {

        ClassPathResource resource =
                new ClassPathResource("valid-qr.jpg");

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "valid-qr.jpg",
                        "image/jpg",
                        resource.getInputStream()
                );

        String result =
                QRDecoderUtil.decodeQRFromImage(file);

        assertThat(result)
                .isEqualTo("00020101021138540010A00000072701240006970436011010275404130208QRIBFTTA53037045802VN6304E472");
    }

    @Test
    @DisplayName("TC-DEC-02 No QR Found")
    void decodeQRFromImage_whenNoQR_shouldThrowException()
            throws Exception {

        BufferedImage image =
                new BufferedImage(
                        200,
                        200,
                        BufferedImage.TYPE_INT_RGB
                );

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        ImageIO.write(image, "png", baos);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.png",
                        "image/png",
                        baos.toByteArray()
                );

        assertThatThrownBy(() ->
                QRDecoderUtil.decodeQRFromImage(file))
                .isInstanceOf(QRScanException.class)
                .hasMessage("No QR code found in the image");
    }

    @Test
    @DisplayName("TC-DEC-03 Invalid Image Content")
    void decodeQRFromImage_whenInvalidImage_shouldThrowException() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "fake.png",
                        "image/png",
                        "abc".getBytes()
                );

        assertThatThrownBy(() ->
                QRDecoderUtil.decodeQRFromImage(file))
                .isInstanceOf(QRScanException.class)
                .hasMessageContaining("Unable to read image file");
    }

    @Test
    @DisplayName("TC-DEC-04 IOException While Reading File")
    void decodeQRFromImage_whenIOException_shouldThrowException()
            throws Exception {

        MultipartFile file = mock(MultipartFile.class);

        when(file.getInputStream())
                .thenThrow(new IOException("boom"));

        assertThatThrownBy(() ->
                QRDecoderUtil.decodeQRFromImage(file))
                .isInstanceOf(QRScanException.class)
                .hasMessageContaining("Error reading image file");
    }
}