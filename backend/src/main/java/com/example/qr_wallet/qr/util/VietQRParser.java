package com.example.qr_wallet.qr.util;

import com.example.qr_wallet.qr.exception.QRScanException;

import java.util.HashMap;
import java.util.Map;

public class VietQRParser {

	private static final Map<String, String> BANK_CODE_MAP = new HashMap<>();

	static {

		BANK_CODE_MAP.put("970422", "MB Bank");
		BANK_CODE_MAP.put("970406", "Agribank");
		BANK_CODE_MAP.put("970407", "Vietcombank");
		BANK_CODE_MAP.put("970408", "Incombank");
		BANK_CODE_MAP.put("970410", "BIDV");
		BANK_CODE_MAP.put("970414", "VietinBank");
		BANK_CODE_MAP.put("970415", "VPBank");
		BANK_CODE_MAP.put("970418", "ACB");
		BANK_CODE_MAP.put("970423", "TPBank");
		BANK_CODE_MAP.put("970436", "Vietcombank");
	}

	private VietQRParser() {
	}

	public static VietQRData parse(String rawQRData) {

		if (rawQRData == null || rawQRData.isBlank()) {
			throw new QRScanException("QR data is empty");
		}

		try {

			Map<String, String> rootFields = parseTLV(rawQRData);

			// Extract account info (bank code + account number) from
			// Merchant Account Information (tag 38 or fallback to 26)
			AccountInfo accountInfo = extractAccountInfo(rootFields);

			String bankCode = accountInfo.bankCode();
			String bankName = getBankName(bankCode);
			String accountNumber = accountInfo.accountNumber();

			String accountName = extractAccountName(rootFields);

			Long amount = extractAmount(rootFields);

			String description = extractDescription(rootFields);

			return new VietQRData(
					rawQRData,
					bankCode,
					bankName,
					accountNumber,
					accountName,
					amount,
					description
			);

		} catch (Exception e) {

			throw new QRScanException(
					"Failed to parse VietQR",
					e
			);
		}
	}

	/**
	 * Parse TLV format:
	 * Tag(2) + Length(2) + Value(n)
	 */
	private static Map<String, String> parseTLV(String data) {

		Map<String, String> fields = new HashMap<>();

		int pos = 0;

		while (pos + 4 <= data.length()) {

			String tag = data.substring(pos, pos + 2);

			String lengthStr = data.substring(pos + 2, pos + 4);

			int length;

			try {

				length = Integer.parseInt(lengthStr);

			} catch (NumberFormatException e) {
				break;
			}

			pos += 4;

			if (pos + length > data.length()) {
				break;
			}

			String value = data.substring(pos, pos + length);

			fields.put(tag, value);

			pos += length;
		}

		return fields;
	}

	private record AccountInfo(
			String bankCode,
			String accountNumber
	) {}

	/**
	 * Extract both bank code (BIN) and account number from Merchant Account Info (tag 38 / 26).
	 */
	private static AccountInfo extractAccountInfo(Map<String, String> fields) {

		String merchantInfo = fields.get("38");

		if (merchantInfo == null) {
			merchantInfo = fields.get("26");
		}

		if (merchantInfo == null) {
			return new AccountInfo("", "");
		}

		try {
			Map<String, String> nested = parseTLV(merchantInfo);
			String accountData = nested.get("01");

			if (accountData == null || accountData.isBlank()) {
				return new AccountInfo("", "");
			}

			// Try to find bank BIN inside accountData
			for (String bankBin : BANK_CODE_MAP.keySet()) {
				int idx = accountData.indexOf(bankBin);
				if (idx >= 0) {
					// Extract remainder after BIN
					String remainder = accountData.substring(idx + bankBin.length());
					// Remove leading zeros
					remainder = remainder.replaceFirst("^0+", "");

					// Heuristic: if remainder is longer than 10 digits, take last 10 digits
					String accountNumber = remainder;
					if (accountNumber.length() > 10) {
						accountNumber = accountNumber.substring(accountNumber.length() - 10);
					}

					return new AccountInfo(bankBin, accountNumber);
				}
			}

			// If no BIN found, fallback: remove leading zeros and, if long, take last 10 digits
			String cleaned = accountData.replaceFirst("^0+", "");
			if (cleaned.length() > 10) {
				cleaned = cleaned.substring(cleaned.length() - 10);
			}

			return new AccountInfo("", cleaned);

		} catch (Exception e) {
			return new AccountInfo("", "");
		}
	}

	/**
	 * Tag 54 = amount
	 */
	private static Long extractAmount(Map<String, String> fields) {

		String amount = fields.get("54");

		if (amount == null || amount.isBlank()) {
			return 0L;
		}

		try {
			return Long.parseLong(amount);
		} catch (Exception e) {
			return 0L;
		}
	}

	/**
	 * Tag 62 = additional data
	 */
	private static String extractDescription(Map<String, String> fields) {

		String additionalData = fields.get("62");

		if (additionalData == null) {
			return "";
		}

		try {
			Map<String, String> nested = parseTLV(additionalData);

			String description = nested.get("08");

			return description != null ? description : "";

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Tag 59 = beneficiary name
	 */
	private static String extractAccountName(Map<String, String> fields) {

		String name = fields.get("59");

		return name != null ? name : "";
	}

	private static String getBankName(String bankCode) {
		return BANK_CODE_MAP.getOrDefault(bankCode, "Unknown Bank");
	}

	public static class VietQRData {

		public final String rawData;

		public final String bankCode;

		public final String bankName;

		public final String accountNumber;

		public final String accountName;

		public final Long amount;

		public final String description;

		public VietQRData(
				String rawData,
				String bankCode,
				String bankName,
				String accountNumber,
				String accountName,
				Long amount,
				String description
		) {

			this.rawData = rawData;
			this.bankCode = bankCode;
			this.bankName = bankName;
			this.accountNumber = accountNumber;
			this.accountName = accountName;
			this.amount = amount;
			this.description = description;
		}
	}

}


