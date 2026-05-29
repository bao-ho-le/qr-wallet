package com.example.qr_wallet.qr.util;

import com.example.qr_wallet.qr.exception.QRScanException;
import org.springframework.web.multipart.MultipartFile;

public class FileValidationUtil {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"};
    private static final String[] ALLOWED_MIME_TYPES = {
            "image/jpeg",
            "image/png",
            "image/jpg"
    };

    private FileValidationUtil() {
        // Utility class - no instantiation
    }

    public static void validateQRImageFile(MultipartFile file) {
        // Check if file is null
        if (file == null || file.isEmpty()) {
            throw new QRScanException("File is required and cannot be empty");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new QRScanException("File size exceeds maximum limit of 10MB");
        }

        // Check file extension
        String fileName = file.getOriginalFilename();
        if (fileName == null || !hasValidExtension(fileName)) {
            throw new QRScanException("Invalid file format. Only PNG, JPG, JPEG are allowed");
        }

        // Check MIME type
        String mimeType = file.getContentType();
        if (!isValidMimeType(mimeType)) {
            throw new QRScanException("Invalid file type. Only image files are allowed");
        }
    }

    private static boolean hasValidExtension(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowed)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        for (String allowed : ALLOWED_MIME_TYPES) {
            if (mimeType.equals(allowed)) {
                return true;
            }
        }
        return false;
    }

    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
}

