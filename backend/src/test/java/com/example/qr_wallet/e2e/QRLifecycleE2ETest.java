package com.example.qr_wallet.e2e;

import com.example.qr_wallet.qr.QR;
import com.example.qr_wallet.qr.QRRepo;
import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(
        webEnvironment =
                SpringBootTest
                        .WebEnvironment
                        .RANDOM_PORT
)
class QRLifecycleE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QRRepo repo;

    @Test
    void qrLifecycle_shouldWorkSuccessfully() {
        QR savedQR = repo.saveAndFlush(QR.builder()
                .name("Old Name")
                .bank("Old Bank")
                .accountNo("1111111114")
                .qrData("old-qr-data")
                .note("old note")
                .build());


        ResponseEntity<QRDetailRes> getResponse = restTemplate.getForEntity(
                "/api/v1/qr/{id}",
                QRDetailRes.class,
                savedQR.getId()
        );

        Assertions.assertEquals(200, getResponse.getStatusCode().value());
        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(savedQR.getId(), getResponse.getBody().id());
        Assertions.assertEquals("Old Name", getResponse.getBody().name());
        Assertions.assertEquals("Old Bank", getResponse.getBody().bank());
        Assertions.assertEquals("1111111114", getResponse.getBody().accountNo());
        Assertions.assertEquals("old-qr-data", getResponse.getBody().qrData());
        Assertions.assertEquals("old note", getResponse.getBody().note());
        Assertions.assertNotNull(getResponse.getBody().createdAt());
        Assertions.assertNotNull(getResponse.getBody().updatedAt());

        UpdateQRRequest updateRequest = new UpdateQRRequest(
                "Updated Name",
                "updated note"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateQRRequest> requestEntity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<QRDetailRes> updateResponse = restTemplate.exchange(
                "/api/v1/qr/{id}",
                HttpMethod.PUT,
                requestEntity,
                QRDetailRes.class,
                savedQR.getId()
        );

        Assertions.assertEquals(200, updateResponse.getStatusCode().value());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertEquals(savedQR.getId(), updateResponse.getBody().id());
        Assertions.assertEquals("Updated Name", updateResponse.getBody().name());
        Assertions.assertEquals("Old Bank", updateResponse.getBody().bank());
        Assertions.assertEquals("1111111114", updateResponse.getBody().accountNo());
        Assertions.assertEquals("old-qr-data", updateResponse.getBody().qrData());
        Assertions.assertEquals("updated note", updateResponse.getBody().note());
        Assertions.assertNotNull(updateResponse.getBody().createdAt());
        Assertions.assertNotNull(updateResponse.getBody().updatedAt());

        QR updatedQR = repo.findById(savedQR.getId()).orElseThrow();
        Assertions.assertEquals("Updated Name", updatedQR.getName());
        Assertions.assertEquals("Old Bank", updatedQR.getBank());
        Assertions.assertEquals("1111111114", updatedQR.getAccountNo());
        Assertions.assertEquals("old-qr-data", updatedQR.getQrData());
        Assertions.assertEquals("updated note", updatedQR.getNote());
        Assertions.assertNotNull(updatedQR.getCreatedAt());
        Assertions.assertNotNull(updatedQR.getUpdatedAt());

        ResponseEntity<QRDetailRes> updatedGetResponse = restTemplate.getForEntity(
                "/api/v1/qr/{id}",
                QRDetailRes.class,
                savedQR.getId()
        );

        Assertions.assertEquals(200, updatedGetResponse.getStatusCode().value());
        Assertions.assertNotNull(updatedGetResponse.getBody());
        Assertions.assertEquals("Updated Name", updatedGetResponse.getBody().name());
        Assertions.assertEquals("Old Bank", updatedGetResponse.getBody().bank());
        Assertions.assertEquals("1111111114", updatedGetResponse.getBody().accountNo());
        Assertions.assertEquals("old-qr-data", updatedGetResponse.getBody().qrData());
        Assertions.assertEquals("updated note", updatedGetResponse.getBody().note());
        Assertions.assertNotNull(updatedGetResponse.getBody().createdAt());
        Assertions.assertNotNull(updatedGetResponse.getBody().updatedAt());

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/api/v1/qr/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class,
                savedQR.getId()
        );

        Assertions.assertEquals(200, deleteResponse.getStatusCode().value());
        Assertions.assertEquals("QR deleted successfully", deleteResponse.getBody());
        Assertions.assertFalse(repo.existsById(savedQR.getId()));

        ResponseEntity<String> deletedGetResponse = restTemplate.getForEntity(
                "/api/v1/qr/{id}",
                String.class,
                savedQR.getId()
        );

        Assertions.assertEquals(404, deletedGetResponse.getStatusCode().value());
        Assertions.assertEquals("QR not found", deletedGetResponse.getBody());
    }

    @BeforeEach
    void clean() {
        repo.deleteAll();
    }
}
