package com.example.qr_wallet.qr.scanQR;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("TC-INT-01 Scan QR Success")
    void uploadAndScanQR_whenValidQR_shouldReturn200() throws Exception {

        ClassPathResource resource =
                new ClassPathResource("valid-qr.jpg");

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "valid-qr.jpg",
                        "image/jpg",
                        resource.getInputStream()
                );

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawData")
                        .value("00020101021138540010A00000072701240006970436011010275404130208QRIBFTTA53037045802VN6304E472"))
                .andExpect(jsonPath("$.bankCode")
                        .value("970436"))
                .andExpect(jsonPath("$.bankName")
                        .value("Vietcombank"))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1027540413"))
                .andExpect(jsonPath("$.accountName")
                        .value(""))
                .andExpect(jsonPath("$.amount")
                        .value(0))
                .andExpect(jsonPath("$.description")
                        .value(""))
                .andExpect(jsonPath("$.requireAccountName")
                        .value(true));
    }

    @Test
    @DisplayName("TC-INT-02 Empty File")
    void uploadAndScanQR_whenFileEmpty_shouldReturn400()
            throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "",
                        "image/png",
                        new byte[0]
                );

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string("File is required and cannot be empty"));
    }

    @Test
    @DisplayName("TC-INT-03 Invalid Extension")
    void uploadAndScanQR_whenExtensionInvalid_shouldReturn400()
            throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.txt",
                        "text/plain",
                        "hello".getBytes()
                );

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string("Invalid file format. Only PNG, JPG, JPEG are allowed"));
    }

    @Test
    @DisplayName("TC-INT-04 Invalid Image Content")
    void uploadAndScanQR_whenImageInvalid_shouldReturn400()
            throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "fake.png",
                        "image/png",
                        "not-an-image".getBytes()
                );

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string("Uploaded file is not a valid image"));
    }

    @Test
    @DisplayName("TC-INT-05 No QR Found")
    void uploadAndScanQR_whenNoQR_shouldReturn400()
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
                        "blank.png",
                        "image/png",
                        baos.toByteArray()
                );

        mockMvc.perform(
                        multipart("/api/v1/qr/scan")
                                .file(file)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string("No QR code found in the image"));
    }
}