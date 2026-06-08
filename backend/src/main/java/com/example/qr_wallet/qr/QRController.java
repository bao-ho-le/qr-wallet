package com.example.qr_wallet.qr;

import com.example.qr_wallet.qr.dto.request.CreateQRRequest;
import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.example.qr_wallet.qr.dto.response.QRScanRes;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.qr_wallet.qr.dto.response.QRListItemRes;
import java.util.List;

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

    @PostMapping( value ="/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QRScanRes> uploadAndScanQR(
            @RequestParam("file") MultipartFile file) {

        QRScanRes result = service.uploadAndScanQR(file);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping
    public ResponseEntity<QRDetailRes> createQR(
            @Valid @RequestBody CreateQRRequest request) {

        return ResponseEntity.ok(
                service.createQR(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<QRListItemRes>> getAllQRs(){
        List<QRListItemRes> items = service.getAllQRs();

        return ResponseEntity.ok(items);
    }


}
