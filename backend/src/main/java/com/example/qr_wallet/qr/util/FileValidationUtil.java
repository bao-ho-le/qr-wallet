package com.example.qr_wallet.qr.util;

import com.example.qr_wallet.qr.exception.QRScanException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

public final class FileValidationUtil {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png"
    );

    private FileValidationUtil() {
    }

    public static void validateQRImageFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new QRScanException(
                    "File is required and cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new QRScanException(
                    "File size exceeds maximum limit of 10MB");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !hasValidExtension(fileName)) {
            throw new QRScanException(
                    "Invalid file format. Only PNG, JPG, JPEG are allowed");
        }

        validateImageContent(file);

    }

    private static boolean hasValidExtension(String fileName) {

        int lastDot = fileName.lastIndexOf('.');

        if (lastDot < 0) {
            return false;
        }

        String extension =
                fileName.substring(lastDot + 1).toLowerCase();

        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private static void validateImageContent(MultipartFile file) {

        try {
            BufferedImage image =
                    ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new QRScanException(
                        "Uploaded file is not a valid image");
            }

        } catch (IOException e) {
            throw new QRScanException(
                    "Failed to read uploaded image");
        }
    }
}