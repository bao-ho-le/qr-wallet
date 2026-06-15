package com.example.qr_wallet.qr.getAllQRs;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QRRepo repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
    }

    @Test
    @DisplayName("TC-INT-01 Get All QR Success")
    void getAllQRs_whenQRsExist_shouldReturn200AndData()
            throws Exception {

        LocalDateTime now = LocalDateTime.now();

        QR qr = new QR();
        qr.setName("Nguyen Van A");
        qr.setBank("VCB");
        qr.setAccountNo("123456789");
        qr.setNote("Personal account");
        qr.setCreatedAt(now);
        qr.setUpdatedAt(now);

        repo.save(qr);

        mockMvc.perform(get("/api/v1/qr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Nguyen Van A"))
                .andExpect(jsonPath("$[0].bank")
                        .value("VCB"))
                .andExpect(jsonPath("$[0].accountNo")
                        .value("123456789"))
                .andExpect(jsonPath("$[0].note")
                        .value("Personal account"))
                .andExpect(jsonPath("$[0].id")
                        .exists())
                .andExpect(jsonPath("$[0].updatedAt")
                        .exists());
    }

    @Test
    @DisplayName("TC-INT-02 Get All QR Empty List")
    void getAllQRs_whenDatabaseEmpty_shouldReturn200AndEmptyList()
            throws Exception {

        mockMvc.perform(get("/api/v1/qr"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("TC-INT-03 Get All QR Ordered By UpdatedAt Desc")
    void getAllQRs_whenMultipleQRsExist_shouldReturnOrderedByUpdatedAtDesc()
            throws Exception {

        LocalDateTime oldTime =
                LocalDateTime.of(2025, 1, 1, 10, 0);

        LocalDateTime newTime =
                LocalDateTime.of(2025, 1, 2, 10, 0);

        QR oldQR = new QR();
        oldQR.setName("Old QR");
        oldQR.setBank("VCB");
        oldQR.setAccountNo("111111111");
        oldQR.setNote("Old");
        oldQR.setCreatedAt(oldTime);
        oldQR.setUpdatedAt(oldTime);

        QR newQR = new QR();
        newQR.setName("New QR");
        newQR.setBank("MB");
        newQR.setAccountNo("222222222");
        newQR.setNote("New");
        newQR.setCreatedAt(newTime);
        newQR.setUpdatedAt(newTime);

        repo.save(oldQR);
        repo.save(newQR);

        mockMvc.perform(get("/api/v1/qr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))

                // item đầu tiên phải là QR mới hơn
                .andExpect(jsonPath("$[0].name")
                        .value("New QR"))
                .andExpect(jsonPath("$[0].bank")
                        .value("MB"))
                .andExpect(jsonPath("$[0].accountNo")
                        .value("222222222"))

                // item thứ hai là QR cũ hơn
                .andExpect(jsonPath("$[1].name")
                        .value("Old QR"))
                .andExpect(jsonPath("$[1].bank")
                        .value("VCB"))
                .andExpect(jsonPath("$[1].accountNo")
                        .value("111111111"));
    }
}