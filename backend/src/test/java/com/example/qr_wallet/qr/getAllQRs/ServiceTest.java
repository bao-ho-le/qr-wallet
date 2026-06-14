package com.example.qr_wallet.qr.getAllQRs;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.QRService;
import com.example.qr_wallet.qr.dto.response.QRListItemRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    private QRRepo repo;

    @InjectMocks
    private QRService service;

    @Test
    @DisplayName("TC-SER-01 Get All QR Success")
    void getAllQRs_whenQRsExist_shouldReturnMappedDTOs() {

        LocalDateTime now = LocalDateTime.now();

        QR qr1 = QR.builder()
                .id(1L)
                .name("Nguyen Van A")
                .bank("VCB")
                .accountNo("123456789")
                .note("Note 1")
                .updatedAt(now)
                .build();


        QR qr2 = QR.builder()
                .id(2L)
                .name("Tran Van B")
                .bank("MB")
                .accountNo("987654321")
                .note("Note 2")
                .updatedAt(now)
                .build();

        when(repo.findAllByOrderByUpdatedAtDesc())
                .thenReturn(List.of(qr1, qr2));

        List<QRListItemRes> result =
                service.getAllQRs();

        assertThat(result).hasSize(2);

        assertThat(result.get(0).id())
                .isEqualTo(1L);
        assertThat(result.get(0).name())
                .isEqualTo("Nguyen Van A");
        assertThat(result.get(0).bank())
                .isEqualTo("VCB");
        assertThat(result.get(0).accountNo())
                .isEqualTo("123456789");
        assertThat(result.get(0).note())
                .isEqualTo("Note 1");
        assertThat(result.get(0).updatedAt())
                .isEqualTo(now);

        assertThat(result.get(1).id())
                .isEqualTo(2L);
        assertThat(result.get(1).name())
                .isEqualTo("Tran Van B");

        verify(repo, times(1))
                .findAllByOrderByUpdatedAtDesc();
    }

    @Test
    @DisplayName("TC-SER-02 Get All QR Empty List")
    void getAllQRs_whenRepositoryReturnsEmpty_shouldReturnEmptyList() {

        when(repo.findAllByOrderByUpdatedAtDesc())
                .thenReturn(Collections.emptyList());

        List<QRListItemRes> result =
                service.getAllQRs();

        assertThat(result).isEmpty();

        verify(repo, times(1))
                .findAllByOrderByUpdatedAtDesc();
    }
}