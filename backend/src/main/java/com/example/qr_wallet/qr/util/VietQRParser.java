
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

            Map<String, String> rootFields =
                    parseTLV(rawQRData);

            String bankCode =
                    extractBankCode(rawQRData);

            String bankName =
                    getBankName(bankCode);

            String accountNumber =
                    extractAccountNumber(rootFields);

            String accountName =
                    extractAccountName(rootFields);

            Long amount =
                    extractAmount(rootFields);

            String description =
                    extractDescription(rootFields);

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

        Map<String, String> fields =
                new HashMap<>();

        int pos = 0;

        while (pos + 4 <= data.length()) {

            String tag =
                    data.substring(pos, pos + 2);

            String lengthStr =
                    data.substring(pos + 2, pos + 4);

            int length;

            try {

                length =
                        Integer.parseInt(lengthStr);

            } catch (NumberFormatException e) {
                break;
            }

            pos += 4;

            if (pos + length > data.length()) {
                break;
            }

            String value =
                    data.substring(pos, pos + length);

            fields.put(tag, value);

            pos += length;
        }

        return fields;
    }

    /**
     * Practical approach:
     * find known bank BIN directly
     * from raw QR string
     */
    private static String extractBankCode(
            String rawQRData
    ) {

        for (String bankBin : BANK_CODE_MAP.keySet()) {

            if (rawQRData.contains(bankBin)) {
                return bankBin;
            }
        }

        return "";
    }

    /**
     * Practical heuristic extraction
     */
    private static String extractAccountNumber(
            Map<String, String> fields
    ) {

        String merchantInfo = fields.get("38");

        if (merchantInfo == null) {
            merchantInfo = fields.get("26");
        }

        if (merchantInfo == null) {
            return "";
        }

        try {

            Map<String, String> nested =
                    parseTLV(merchantInfo);

            String accountData =
                    nested.get("01");

            if (accountData == null ||
                    accountData.length() < 10) {

                return "";
            }

            /*
             * Remove leading zeros
             * sometimes QR contains:
             * 000697043601101027540413
             */

            accountData =
                    accountData.replaceFirst("^0+", "");

            /*
             * Remove bank BIN if exists
             */

            for (String bankBin : BANK_CODE_MAP.keySet()) {

                if (accountData.startsWith(bankBin)) {

                    return accountData.substring(
                            bankBin.length()
                    );
                }
            }

            return accountData;

        } catch (Exception e) {

            return "";
        }
    }

    /**
     * Tag 54 = amount
     */
    private static Long extractAmount(
            Map<String, String> fields
    ) {

        String amount =
                fields.get("54");

        if (amount == null ||
                amount.isBlank()) {

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
    private static String extractDescription(
            Map<String, String> fields
    ) {

        String additionalData =
                fields.get("62");

        if (additionalData == null) {
            return "";
        }

        try {

            Map<String, String> nested =
                    parseTLV(additionalData);

            /*
             * Tag 08 often contains
             * transfer content
             */

            String description =
                    nested.get("08");

            return description != null
                    ? description
                    : "";

        } catch (Exception e) {

            return "";
        }
    }

    /**
     * Tag 59 = beneficiary name
     */
    private static String extractAccountName(
            Map<String, String> fields
    ) {

        String name =
                fields.get("59");

        return name != null
                ? name
                : "";
    }

    private static String getBankName(
            String bankCode
    ) {

        return BANK_CODE_MAP.getOrDefault(
                bankCode,
                "Unknown Bank"
        );
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
