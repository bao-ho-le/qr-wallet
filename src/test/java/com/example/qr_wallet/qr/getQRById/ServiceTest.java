package com.example.qr_wallet.qr.getQRById;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.example.qr_wallet.qr.exception.QRNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private QRRepo repo;

    @InjectMocks
    private QRService service;

    @Test
    void getQRById_whenQRFound_shouldReturnQr(){

        QR qr = QR.builder()
                .id(1L)
                .bank("Momo")
                .build();

        when(repo.findById(1L))
                .thenReturn(Optional.of(qr));

        QRDetailRes result = service.getQRById(1L);
        assertEquals("Momo", result.bank());
    }

    @Test
    void getQRById_whenQRNotFound_shouldThrowException(){

        // Giả lập không tìm thấy QR với id = 1
        when(repo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(QRNotFoundException.class,
                () -> service.getQRById(1L));
    }

    @Test
    void getQRById_whenIdIsNull_shouldThrowException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.getQRById(null));
    }

    @Test
    void getQRById_whenIdIsNegative_shouldThrowException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.getQRById(-1L));
    }

    @Test
    void getQRById_whenIdIsZero_shouldThrowException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.getQRById(0L));
    }


}
