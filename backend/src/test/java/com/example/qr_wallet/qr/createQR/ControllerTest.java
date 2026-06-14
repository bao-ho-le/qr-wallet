package com.example.qr_wallet.qr.createQR;

import com.example.qr_wallet.qr.QRController;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.request.CreateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QRController.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRService qrService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TC-CTRL-01 Create QR Success")
    void createQR_whenRequestIsValid_shouldCallServiceAndReturn200() throws Exception {

        LocalDateTime now = LocalDateTime.now();

        QRDetailRes response = new QRDetailRes(
                1L,
                "Nguyen Van A",
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                "Test note",
                now,
                now
        );

        when(qrService.createQR(any())).thenReturn(response);

        CreateQRRequest request = new CreateQRRequest(
                "Nguyen Van A",
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                "Test note"
        );

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nguyen Van A"))
                .andExpect(jsonPath("$.bank").value("VCB"))
                .andExpect(jsonPath("$.accountNo").value("123456789"))
                .andExpect(jsonPath("$.qrData").value("RAW_QR_DATA"))
                .andExpect(jsonPath("$.note").value("Test note"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(qrService, times(1))
                .createQR(any(CreateQRRequest.class));
    }

    @Test
    @DisplayName("TC-CTRL-02 Name Null")
    void createQR_whenNameIsNull_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                null,
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-03 Bank Null")
    void createQR_whenBankIsNull_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "name",
                null,
                "123456789",
                "RAW_QR_DATA",
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-04 AccountNo Null")
    void createQR_whenAccountNoIsNull_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "namse",
                "mbank",
                null,
                "RAW_QR_DATA",
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-05 QRData Null")
    void createQR_whenQrDataIsNull_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "namse",
                "mbank",
                "1209124024",
                null,
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-06 Name Blank")
    void createQR_whenNameIsBlank_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "",
                "mbank",
                "34342124024",
                "qr_data",
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-07 Bank Blank")
    void createQR_whenBankIsBlank_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "ten",
                "",
                "11342124024",
                "q_data",
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-08 AccountNo Blank")
    void createQR_whenAccountNoIsBlank_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "nem",
                "bank",
                "",
                "qr_data",
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }

    @Test
    @DisplayName("TC-CTRL-09 QRData Blank")
    void createQR_whenQrDataIsBlank_shouldReturn400AndNotCallService() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "net",
                "mbank",
                "2212124024",
                "",
                "note");

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(qrService, never()).createQR(any());
    }
}