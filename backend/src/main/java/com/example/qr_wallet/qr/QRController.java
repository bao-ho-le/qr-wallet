package com.example.qr_wallet.qr;

import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/qr")
public class QRController {
    private final QRService service;

    public QRController(QRService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<QRDetailRes> getQRById(@PathVariable Long id){
        QRDetailRes qrDetailRes = service.getQRById(id);

        return ResponseEntity.ok(qrDetailRes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQRById(@PathVariable Long id){
        service.deleteQRById(id);

        return ResponseEntity.ok("QR deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<QRDetailRes> updateQRById(
            @PathVariable Long id, @Valid @RequestBody UpdateQRRequest request){

        QRDetailRes updatedQR = service.updateQRById(id, request);

        return ResponseEntity.ok(updatedQR);
    }


}
