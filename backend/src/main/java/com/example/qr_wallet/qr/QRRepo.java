package com.example.qr_wallet.qr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRRepo extends JpaRepository<QR, Long> {

}