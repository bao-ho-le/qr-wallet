package com.example.qr_wallet.qr.util;

import com.example.qr_wallet.qr.exception.QRScanException;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRDecoderUtil {

    private QRDecoderUtil() {
        // Utility class - no instantiation
    }

    public static String decodeQRFromImage(MultipartFile file) {
        try {
            // Read image from file
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new QRScanException("Unable to read image file");
            }

            // Decode QR code
            return decodeQRImage(image);

        } catch (IOException e) {
            throw new QRScanException("Error reading image file: " + e.getMessage(), e);
        } catch (QRScanException e) {
            throw e;
        } catch (Exception e) {
            throw new QRScanException("Error decoding QR code: " + e.getMessage(), e);
        }
    }

    private static String decodeQRImage(BufferedImage image) throws Exception {
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        MultiFormatReader reader = new MultiFormatReader();

        try {
            Result result = reader.decode(bitmap, hints);
            String decodedText = result.getText();

            if (decodedText == null || decodedText.trim().isEmpty()) {
                throw new QRScanException("QR code is empty or could not be decoded");
            }

            return decodedText;

        } catch (NotFoundException e) {
            throw new QRScanException("No QR code found in the image");
        }
    }
}

