package com.cine.complete.payu;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PayuSignature {

    private PayuSignature() {
    }

    public static String formatAmount(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        if (scaled.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
            return scaled.setScale(0, RoundingMode.UNNECESSARY).toPlainString();
        }
        if (scaled.remainder(new BigDecimal("0.10")).compareTo(BigDecimal.ZERO) == 0) {
            return scaled.setScale(1, RoundingMode.UNNECESSARY).toPlainString();
        }
        return scaled.toPlainString();
    }

    public static String sign(String apiKey, String merchantId, String referenceCode, BigDecimal amount, String currency) {
        String raw = apiKey + "~" + merchantId + "~" + referenceCode + "~" + formatAmount(amount) + "~" + currency;
        return md5(raw);
    }

    private static String md5(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte current : bytes) {
                hex.append(String.format("%02x", current));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 no está disponible", exception);
        }
    }
}
