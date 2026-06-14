package com.example.qr_wallet.qr.createQR;


import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.request.CreateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.exception.QRAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    private QRRepo qrRepository;

    @InjectMocks
    private QRService qrService;

    @Test
    @DisplayName("TC-SER-01 Create QR Success")
    void createQR_whenRequestIsValid_shouldSaveAndReturnResponse() {

        CreateQRRequest request = new CreateQRRequest(
                "Nguyen Van A",
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                "Test note"
        );

        LocalDateTime now = LocalDateTime.now();

        QR savedQr = QR.builder()
                .id(1L)
                .name("Nguyen Van A")
                .bank("VCB")
                .accountNo("123456789")
                .qrData("RAW_QR_DATA")
                .note("Test note")
                .createdAt(now)
                .updatedAt(now)
                .build();


        when(qrRepository.save(any(QR.class)))
                .thenReturn(savedQr);

        QRDetailRes result = qrService.createQR(request);

        assertEquals(savedQr.getId(), result.id());
        assertEquals(savedQr.getName(), result.name());
        assertEquals(savedQr.getBank(), result.bank());
        assertEquals(savedQr.getAccountNo(), result.accountNo());

        verify(qrRepository, times(1))
                .save(any(QR.class));
    }

    @Test
    @DisplayName("TC-SER-02 Create QR Duplicate")
    void createQR_whenQrAlreadyExists_shouldThrowException() {

        CreateQRRequest request = new CreateQRRequest(
                "Nguyen Van A",
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                "Test note"
        );

        when(qrRepository.existsByBankAndAccountNo(
                "VCB",
                "123456789"))
                .thenReturn(true);

        assertThrows(
                QRAlreadyExistsException.class,
                () -> qrService.createQR(request)
        );

        verify(qrRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("TC-SER-03 Create QR With Null Note")
    void createQR_whenNoteIsNull_shouldSaveSuccessfully() {

        CreateQRRequest request = new CreateQRRequest(
                "Nguyen Van A",
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                null
        );

        LocalDateTime now = LocalDateTime.now();

        QR savedQr = QR.builder()
                .id(1L)
                .name("Nguyen Van A")
                .bank("VCB")
                .accountNo("123456789")
                .qrData("RAW_QR_DATA")
                .note(null)
                .createdAt(now)
                .updatedAt(now)
                .build();


        when(qrRepository.save(any(QR.class)))
                .thenReturn(savedQr);

        QRDetailRes result = qrService.createQR(request);

        assertNotNull(result);
        assertNull(result.note());

        verify(qrRepository, times(1))
                .save(any(QR.class));
    }
}