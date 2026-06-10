package com.example.qr_wallet.e2e;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.transaction.TestTransaction;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
        webEnvironment =
                SpringBootTest
                        .WebEnvironment
                        .RANDOM_PORT
)
@Transactional
class UpdateValidationE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QRRepo repo;

    @Test
    void updateQRWithInvalidData_shouldReturn400AndKeepOriginalData() throws Exception {
        QR savedQR = repo.saveAndFlush(QR.builder()
                .name("Original Name")
                .bank("Original Bank")
                .accountNo("111111111")
                .qrData("original-qr-data")
                .note("original note")
                .build());

        TestTransaction.flagForCommit();
        TestTransaction.end();

        UpdateQRRequest request = new UpdateQRRequest(
                "",
                "updated note"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(request), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/qr/{id}",
                HttpMethod.PUT,
                requestEntity,
                String.class,
                savedQR.getId()
        );

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().contains("\"name\":\"Name is required\""));

        QR unchangedQR = repo.findById(savedQR.getId()).orElseThrow();
        Assertions.assertEquals("Original Name", unchangedQR.getName());
        Assertions.assertEquals("Original Bank", unchangedQR.getBank());
        Assertions.assertEquals("111111111", unchangedQR.getAccountNo());
        Assertions.assertEquals("original-qr-data", unchangedQR.getQrData());
        Assertions.assertEquals("original note", unchangedQR.getNote());
        Assertions.assertNotNull(unchangedQR.getCreatedAt());
        Assertions.assertNotNull(unchangedQR.getUpdatedAt());

        repo.deleteById(savedQR.getId());
    }
}
