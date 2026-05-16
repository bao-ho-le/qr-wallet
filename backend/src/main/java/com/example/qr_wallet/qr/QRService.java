package com.example.qr_wallet.qr;

import com.example.qr_wallet.qr.dto.request.UpdateQRRequest;
import com.example.qr_wallet.qr.dto.response.QRDetailRes;
import com.example.qr_wallet.qr.exception.QRNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class QRService {
    private final QRRepo repo;

    public QRService(QRRepo repo) {
        this.repo = repo;
    }

    public QRDetailRes getQRById(Long id){

        if(id == null || id <= 0){
            throw new IllegalArgumentException("Invalid QR id");
        }

        QR qr = repo.findById(id)
                .orElseThrow(() ->
                        new QRNotFoundException("QR not found"));

        return new QRDetailRes(
                qr.getId(),
                qr.getName(),
                qr.getBank(),
                qr.getAccountNo(),
                qr.getQrData(),
                qr.getNote(),
                qr.getCreatedAt(),
                qr.getUpdatedAt()
        );
    }

    public void deleteQRById(Long id){

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid QR id");
        }

        QR qr = repo.findById(id)
                .orElseThrow(() ->
                        new QRNotFoundException("QR not found"));

        repo.delete(qr);
    }

    public QRDetailRes updateQRById(Long id, UpdateQRRequest request){

        if(id == null || id <= 0){
            throw new IllegalArgumentException("Invalid QR id");
        }


        QR qr = repo.findById(id)
                .orElseThrow(() ->
                        new QRNotFoundException("QR not found"));

        qr.setName(request.name());
        qr.setBank(request.bank());
        qr.setAccountNo(request.accountNo());
        qr.setQrData(request.qrData());
        qr.setNote(request.note());


        QR updatedQR = repo.save(qr);

        return new QRDetailRes(
                updatedQR.getId(),
                updatedQR.getName(),
                updatedQR.getBank(),
                updatedQR.getAccountNo(),
                updatedQR.getQrData(),
                updatedQR.getNote(),
                updatedQR.getCreatedAt(),
                updatedQR.getUpdatedAt()
        );
    }
}
