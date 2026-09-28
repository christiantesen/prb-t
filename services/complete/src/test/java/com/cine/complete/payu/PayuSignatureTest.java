package com.cine.complete.payu;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayuSignatureTest {

    @Test
    void dropsTrailingZerosTheWayPayuExpects() {
        assertEquals("100", PayuSignature.formatAmount(new BigDecimal("100.00")));
        assertEquals("100.5", PayuSignature.formatAmount(new BigDecimal("100.50")));
        assertEquals("100.55", PayuSignature.formatAmount(new BigDecimal("100.55")));
    }

    @Test
    void signatureUsesTheFormattedAmount() {
        String signature = PayuSignature.sign(
                "4Vj8eK4rloUd272L48hsrarnUA",
                "508029",
                "CINE1",
                new BigDecimal("32.00"),
                "PEN");
        assertEquals(32, signature.length());
        assertEquals(
                PayuSignature.sign("4Vj8eK4rloUd272L48hsrarnUA", "508029", "CINE1", new BigDecimal("32"), "PEN"),
                signature);
    }
}
