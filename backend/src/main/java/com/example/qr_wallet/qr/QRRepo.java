package com.example.qr_wallet.qr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QRRepo extends JpaRepository<QR, Long> {

	/**
	 * Find all QR entities ordered by updatedAt descending (newest first).
	 */
	List<QR> findAllByOrderByUpdatedAtDesc();
	boolean existsByBankAndAccountNo(
			String bank,
			String accountNo
	);

}