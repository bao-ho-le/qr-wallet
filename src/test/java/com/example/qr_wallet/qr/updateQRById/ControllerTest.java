package com.example.qr_wallet.qr.updateQRById;

import com.example.qr_wallet.qr.QRController;
import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.example.qr_wallet.qr.exception.GlobalExceptionHandler;
import com.example.qr_wallet.qr.exception.QRNotFoundException;
import com.example.qr_wallet.qr.QRService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QRController.class)
@Import(GlobalExceptionHandler.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QRService service;


    @Test
    void updateQRById_whenValidRequest_shouldReturnUpdatedQR() throws Exception {

        Long qrId = 1L;

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "Vietcombank",
                "123456789",
                "qr-data",
                "personal note"
        );

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        QRDetailRes response = new QRDetailRes(
                qrId,
                "Le Vo",
                "Vietcombank",
                "123456789",
                "qr-data",
                "personal note",
                createdAt,
                updatedAt
        );

        when(service.updateQRById(eq(qrId), any(UpdateQRRequest.class)))
                .thenReturn(response);


        mockMvc.perform(put("/api/v1/qr/{id}", qrId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(qrId))
                .andExpect(jsonPath("$.name").value("Le Vo"))
                .andExpect(jsonPath("$.bank").value("Vietcombank"))
                .andExpect(jsonPath("$.accountNo").value("123456789"))
                .andExpect(jsonPath("$.note").value("personal note"));


        verify(service).updateQRById(eq(qrId), any(UpdateQRRequest.class));
    }


    @Test
    void updateQRById_whenQRNotFound_shouldReturn404() throws Exception {

        Long qrId = 999L;

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "Vietcombank",
                "123456789",
                "qr-data",
                "note"
        );

        when(service.updateQRById(eq(qrId), any(UpdateQRRequest.class)))
                .thenThrow(new QRNotFoundException("QR not found"));

        mockMvc.perform(put("/api/v1/qr/{id}", qrId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().string("QR not found"));
    }


    @Test
    void updateQRById_whenIdIsNegative_shouldReturn400() throws Exception {

        Long qrId = -1L;

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "Vietcombank",
                "123456789",
                "qr-data",
                "note"
        );

        when(service.updateQRById(eq(qrId), any(UpdateQRRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid QR id"));

        mockMvc.perform(put("/api/v1/qr/{id}", qrId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }


    @Test
    void updateQRById_whenIdIsZero_shouldReturn400() throws Exception {

        Long qrId = 0L;

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "Vietcombank",
                "123456789",
                "qr-data",
                "note"
        );

        when(service.updateQRById(eq(qrId), any(UpdateQRRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid QR id"));

        mockMvc.perform(put("/api/v1/qr/{id}", qrId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid QR id"));
    }


    @Test
    void updateQRById_whenNameIsBlank_shouldReturn400() throws Exception {

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "Vietcombank",
                "123456789",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"));


        verify(service, never()).updateQRById(any(), any());
    }


    @Test
    void updateQRById_whenBankIsBlank_shouldReturn400() throws Exception {

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "",
                "123456789",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.bank").value("Bank is required"));

        verify(service, never()).updateQRById(any(), any());
    }


    @Test
    void updateQRById_whenAccountNoIsBlank_shouldReturn400() throws Exception {

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "Vietcombank",
                "",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNo").value("Account number is required"));


        verify(service, never()).updateQRById(any(), any());
    }


    @Test
    void updateQRById_whenNameAndBankAreBlank_shouldReturn400() throws Exception {

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "",
                "123456789",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.bank").value("Bank is required"));


        verify(service, never()).updateQRById(any(), any());
    }


    @Test
    void updateQRById_whenNameAndAccountNoAreBlank_shouldReturn400() throws Exception {

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "Vietcombank",
                "",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.accountNo").value("Account number is required"));


        verify(service, never()).updateQRById(any(), any());
    }


    @Test
    void updateQRById_whenBankAndAccountNoAreBlank_shouldReturn400() throws Exception {

        UpdateQRRequest request = new UpdateQRRequest(
                "Le Vo",
                "",
                "",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.bank").value("Bank is required"))
                .andExpect(jsonPath("$.accountNo").value("Account number is required"));


        verify(service, never()).updateQRById(any(), any());
    }


    @Test
    void updateQRById_whenAllRequiredFieldsAreBlank_shouldReturn400() throws Exception {

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "",
                "",
                "qr-data",
                "note"
        );

        mockMvc.perform(put("/api/v1/qr/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.bank").value("Bank is required"))
                .andExpect(jsonPath("$.accountNo").value("Account number is required"));


        verify(service, never()).updateQRById(any(), any());
    }

}