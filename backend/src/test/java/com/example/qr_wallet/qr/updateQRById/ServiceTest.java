package com.example.qr_wallet.qr.updateQRById;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.example.qr_wallet.qr.exception.QRNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.never;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private QRRepo repo;

    @InjectMocks
    private QRService service;

    private final UpdateQRRequest validRequest = new UpdateQRRequest(
            "Le Vo",
            "VCB",
            "123456",
            "qr-data",
            "note"
    );

    @Test
    void updateQRById_whenIdIsNull_shouldThrowException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.updateQRById(null, validRequest));
    }

    @Test
    void updateQRById_whenIdIsNegative_shouldThrowException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.updateQRById(-1L, validRequest));
    }

    @Test
    void updateQRById_whenIdIsZero_shouldThrowException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.updateQRById(0L, validRequest));
    }

    @Test
    void updateQRById_whenQRNotFound_shouldThrowException(){

        // Giả lập không tìm thấy QR với id = 1
        when(repo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(QRNotFoundException.class,
                () -> service.updateQRById(1L, validRequest));

        verify(repo, never()).save(any());
    }

    @Test
    void updateQRById_whenValidRequest_shouldUpdateSuccessfully(){

        QR qr = QR.builder()
                .id(1L)
                .name("Old Name")
                .bank("VCB")
                .accountNo("111")
                .build();

        UpdateQRRequest request =
                new UpdateQRRequest(
                        "New Name",
                        "Momo",
                        "999",
                        "qr-data",
                        "new note"
                );

        when(repo.findById(1L))
                .thenReturn(Optional.of(qr));

        when(repo.save(qr))
                .thenReturn(qr);

        QRDetailRes result =
                service.updateQRById(1L, request);

        assertEquals("New Name", result.name());
        assertEquals("Momo", result.bank());
        assertEquals("999", result.accountNo());
        assertEquals("new note", result.note());
        assertEquals("qr-data", result.qrData());

        verify(repo).save(qr);
    }

}
