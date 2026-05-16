package com.example.qr_wallet.qr.deleteQRById;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.exception.QRNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private QRRepo repo;

    @InjectMocks
    private QRService service;

    @Test
    void deleteQRById_whenQRFound_shouldDeleteQr(){

        QR qr = QR.builder()
                .id(1L)
                .bank("Momo")
                .build();

        when(repo.findById(1L))
                .thenReturn(Optional.of(qr));

        service.deleteQRById(1L);
        verify(repo).delete(qr);
    }

    @Test
    void deleteQRById_whenQRNotFound_shouldThrowException(){

        // Giả lập không tìm thấy QR với id = 1
        when(repo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(QRNotFoundException.class,
                () -> service.deleteQRById(1L));

        verify(repo, never()).delete(any());
    }

    @Test
    void deleteQRById_whenIdIsNull_shouldThrowException(){

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteQRById(null));

        verify(repo, never()).delete(any());
    }

    @Test
    void deleteQRById_whenIdIsNegative_shouldThrowException(){

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteQRById(-1L));

        verify(repo, never()).delete(any());
    }

    @Test
    void deleteQRById_whenIdIsZero_shouldThrowException(){

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteQRById(0L));

        verify(repo, never()).delete(any());
    }
}
