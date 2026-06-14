package com.example.qr_wallet.qr.createQR;

import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.dto.request.CreateQRRequest;
import com.example.qr_wallet.qr.QR;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QRRepo qrRepository;

    @BeforeEach
    void cleanDb() {
        qrRepository.deleteAll();
    }

    @Test
    @DisplayName("TC-INT-01 Create QR Success - should persist data in DB")
    void createQR_whenValidRequest_shouldReturn200AndPersistData() throws Exception {

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
                .andExpect(jsonPath("$.accountNo").value("123456789"));

        // VERIFY DB
        assertThat(qrRepository.findAll()).hasSize(1);

        QR saved = qrRepository.findAll().get(0);
        assertThat(saved.getName()).isEqualTo("Nguyen Van A");
        assertThat(saved.getBank()).isEqualTo("VCB");
        assertThat(saved.getAccountNo()).isEqualTo("123456789");
    }

    @Test
    @DisplayName("TC-INT-02 Duplicate QR should return error and not insert new record")
    void createQR_whenDuplicate_shouldNotCreateNewRecord() throws Exception {

        // GIVEN existing record
        qrRepository.save(QR.builder()
                .name("Nguyen Van A")
                .bank("VCB")
                .accountNo("123456789")
                .qrData("RAW_QR_DATA")
                .note("old")
                .build());

        CreateQRRequest request = new CreateQRRequest(
                "Nguyen Van A",
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                "new note"
        );

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        // VERIFY DB NOT CHANGED
        assertThat(qrRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("TC-INT-03 Validation fail should return 400 and not persist data")
    void createQR_whenMissingField_shouldReturn400() throws Exception {

        String invalidRequest = """
                {
                  "bank": "VCB",
                  "accountNo": "123456789",
                  "qrData": "RAW_QR_DATA"
                }
                """;

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        assertThat(qrRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("TC-INT-04 Blank field should return 400 and not persist")
    void createQR_whenBlankField_shouldReturn400() throws Exception {

        String invalidRequest = """
                {
                  "name": "",
                  "bank": "VCB",
                  "accountNo": "123456789",
                  "qrData": "RAW_QR_DATA"
                }
                """;

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        assertThat(qrRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("TC-INT-05 Create QR with null note should persist successfully")
    void createQR_whenNoteIsNull_shouldPersistSuccessfully() throws Exception {

        CreateQRRequest request = new CreateQRRequest(
                "Nguyen Van A",
                "VCB",
                "123456789",
                "RAW_QR_DATA",
                null
        );

        mockMvc.perform(post("/api/v1/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").doesNotExist());

        QR saved = qrRepository.findAll().get(0);
        assertThat(saved.getNote()).isNull();
    }
}