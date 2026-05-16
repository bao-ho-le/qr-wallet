package com.example.qr_wallet.e2e;

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

@SpringBootTest(
        webEnvironment =
                SpringBootTest
                        .WebEnvironment
                        .RANDOM_PORT
)
@Transactional
class NonExistingQRE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QRRepo repo;

    @Test
    void nonExistingQRFlow_shouldReturn404() {
        Long nonExistingId = 999999999L;

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/api/v1/qr/{id}",
                String.class,
                nonExistingId
        );
        Assertions.assertEquals(404, getResponse.getStatusCode().value());
        Assertions.assertEquals("QR not found", getResponse.getBody());

        UpdateQRRequest request = new UpdateQRRequest(
                "Updated Name",
                "Updated Bank",
                "222222222",
                "updated-qr-data",
                "updated note"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateQRRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> putResponse = restTemplate.exchange(
                "/api/v1/qr/{id}",
                HttpMethod.PUT,
                requestEntity,
                String.class,
                nonExistingId
        );
        Assertions.assertEquals(404, putResponse.getStatusCode().value());
        Assertions.assertEquals("QR not found", putResponse.getBody());

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/api/v1/qr/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class,
                nonExistingId
        );
        Assertions.assertEquals(404, deleteResponse.getStatusCode().value());
        Assertions.assertEquals("QR not found", deleteResponse.getBody());

        Assertions.assertFalse(repo.existsById(nonExistingId));
    }
}
