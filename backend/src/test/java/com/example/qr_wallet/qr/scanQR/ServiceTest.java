package com.example.qr_wallet.qr.scanQR;


import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRScanRes;
import com.example.qr_wallet.qr.util.FileValidationUtil;
import com.example.qr_wallet.qr.util.QRDecoderUtil;
import com.example.qr_wallet.qr.util.VietQRParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ServiceTest {


    @InjectMocks
    private QRService service;

    @Test
    @DisplayName("TC-SER-01 Scan QR Success")
    void uploadAndScanQR_whenAccountNameExists_shouldReturnRequireAccountNameFalse() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "qr.png",
                        "image/png",
                        "dummy".getBytes()
                );

        try (
                MockedStatic<FileValidationUtil> validationMock =
                        mockStatic(FileValidationUtil.class);

                MockedStatic<QRDecoderUtil> decoderMock =
                        mockStatic(QRDecoderUtil.class);

                MockedStatic<VietQRParser> parserMock =
                        mockStatic(VietQRParser.class)
        ) {

            validationMock
                    .when(() -> FileValidationUtil.validateQRImageFile(file))
                    .thenAnswer(inv -> null);


            decoderMock.when(() ->
                            QRDecoderUtil.decodeQRFromImage(file))
                    .thenReturn("RAW_QR_DATA");

            parserMock.when(() ->
                            VietQRParser.parse("RAW_QR_DATA"))
                    .thenReturn(
                            new VietQRParser.VietQRData(
                                    "RAW_QR_DATA",
                                    "970436",
                                    "Vietcombank",
                                    "1027540413",
                                    "Nguyen Van A",
                                    100000L,
                                    "Test transfer"
                            )
                    );

            QRScanRes result =
                    service.uploadAndScanQR(file);

            assertThat(result.rawData())
                    .isEqualTo("RAW_QR_DATA");

            assertThat(result.bankCode())
                    .isEqualTo("970436");

            assertThat(result.bankName())
                    .isEqualTo("Vietcombank");

            assertThat(result.accountNumber())
                    .isEqualTo("1027540413");

            assertThat(result.accountName())
                    .isEqualTo("Nguyen Van A");

            assertThat(result.amount())
                    .isEqualTo(100000L);

            assertThat(result.description())
                    .isEqualTo("Test transfer");

            assertThat(result.requireAccountName())
                    .isFalse();
        }
    }

    @Test
    @DisplayName("TC-SER-02 Scan QR Require Account Name")
    void uploadAndScanQR_whenAccountNameMissing_shouldReturnRequireAccountNameTrue() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "qr.png",
                        "image/png",
                        "dummy".getBytes()
                );

        try (
                MockedStatic<FileValidationUtil> validationMock =
                        mockStatic(FileValidationUtil.class);


                MockedStatic<QRDecoderUtil> decoderMock =
                        mockStatic(QRDecoderUtil.class);

                MockedStatic<VietQRParser> parserMock =
                        mockStatic(VietQRParser.class)
        ) {

            validationMock
                    .when(() -> FileValidationUtil.validateQRImageFile(file))
                    .thenAnswer(inv -> null);


            decoderMock.when(() ->
                            QRDecoderUtil.decodeQRFromImage(file))
                    .thenReturn("RAW_QR_DATA");

            parserMock.when(() ->
                            VietQRParser.parse("RAW_QR_DATA"))
                    .thenReturn(
                            new VietQRParser.VietQRData(
                                    "RAW_QR_DATA",
                                    "970436",
                                    "Vietcombank",
                                    "1027540413",
                                    "",
                                    100000L,
                                    "Test transfer"
                            )
                    );

            QRScanRes result =
                    service.uploadAndScanQR(file);

            assertThat(result.requireAccountName())
                    .isTrue();
        }
    }
}