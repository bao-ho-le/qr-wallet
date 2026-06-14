package com.example.qr_wallet.qr.scanQR;

import com.example.qr_wallet.qr.exception.QRScanException;
import com.example.qr_wallet.qr.util.VietQRParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VietQRParserTest {

    @Test
    @DisplayName("TC-PAR-01 QR Data Null")
    void parse_whenQRDataNull_shouldThrowQRScanException() {

        assertThatThrownBy(() ->
                VietQRParser.parse(null))
                .isInstanceOf(QRScanException.class)
                .hasMessage("QR data is empty");
    }

    @Test
    @DisplayName("TC-PAR-02 QR Data Blank")
    void parse_whenQRDataBlank_shouldThrowQRScanException() {

        assertThatThrownBy(() ->
                VietQRParser.parse(" "))
                .isInstanceOf(QRScanException.class)
                .hasMessage("QR data is empty");
    }

    @Test
    @DisplayName("TC-PAR-03 Missing Merchant Info")
    void parse_whenMerchantInfoMissing_shouldReturnEmptyBankAndAccount() {

        String rawQR = "0002010102115802VN";

        VietQRParser.VietQRData result =
                VietQRParser.parse(rawQR);

        assertThat(result.rawData).isEqualTo(rawQR);
        assertThat(result.bankCode).isEmpty();
        assertThat(result.bankName).isEqualTo("Unknown Bank");
        assertThat(result.accountNumber).isEmpty();
    }

    @Test
    @DisplayName("TC-PAR-04 Parse Success")
    void parse_whenValidQRData_shouldReturnParsedData() {

        String rawQR =
                "00020101021138540010A00000072701240006970436011010275404130208QRIBFTTA53037045802VN6304E472";

        VietQRParser.VietQRData result =
                VietQRParser.parse(rawQR);

        assertThat(result.rawData)
                .isEqualTo(rawQR);

        assertThat(result.bankCode)
                .isEqualTo("970436");

        assertThat(result.bankName)
                .isEqualTo("Vietcombank");

        assertThat(result.accountNumber)
                .isEqualTo("1027540413");

        assertThat(result.accountName)
                .isEmpty();

        assertThat(result.amount)
                .isEqualTo(0L);

        assertThat(result.description)
                .isEmpty();
    }
}