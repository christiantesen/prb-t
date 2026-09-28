package com.cine.complete.payu;

import com.cine.complete.api.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PayuClient {

    private static final Logger log = LoggerFactory.getLogger(PayuClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PayuProperties properties;

    public PayuClient(RestTemplate restTemplate, ObjectMapper objectMapper, PayuProperties properties) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public PayuCharge charge(BigDecimal total, String fullName, String email, String documentNumber,
                             String cardNumber, String expirationDate, String cvv, String paymentMethod,
                             boolean simulateRejection) {
        if (simulateRejection && properties.isTest()) {
            log.info("Pago de prueba rechazado a proposito");
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "El pago no fue aprobado");
        }
        String reference = "CINE" + System.currentTimeMillis();
        String amountText = PayuSignature.formatAmount(total);
        BigDecimal amount = new BigDecimal(amountText);
        String signature = PayuSignature.sign(
                properties.getApiKey(),
                properties.getMerchantId(),
                reference,
                amount,
                "PEN");
        String cardName = properties.isTest()
                ? (simulateRejection ? "REJECTED" : "APPROVED")
                : fullName;

        Map<String, Object> address = new LinkedHashMap<>();
        address.put("street1", "Av. Javier Prado Este 123");
        address.put("city", "Lima");
        address.put("state", "Lima");
        address.put("country", "PE");
        address.put("postalCode", "15036");
        address.put("phone", "999999999");

        Map<String, Object> buyer = new LinkedHashMap<>();
        buyer.put("merchantBuyerId", "1");
        buyer.put("fullName", fullName);
        buyer.put("emailAddress", email);
        buyer.put("contactPhone", "999999999");
        buyer.put("dniNumber", documentNumber);
        buyer.put("shippingAddress", address);

        Map<String, Object> order = new LinkedHashMap<>();
        order.put("accountId", properties.getAccountId());
        order.put("referenceCode", reference);
        order.put("description", "Dulceria Cines Bruma");
        order.put("language", "es");
        order.put("signature", signature);
        order.put("notifyUrl", "https://localhost/payu/notify");
        order.put("additionalValues", Map.of("TX_VALUE", Map.of("value", amount, "currency", "PEN")));
        order.put("buyer", buyer);
        order.put("shippingAddress", address);

        Map<String, Object> payer = new LinkedHashMap<>();
        payer.put("merchantPayerId", "1");
        payer.put("fullName", fullName);
        payer.put("emailAddress", email);
        payer.put("contactPhone", "999999999");
        payer.put("dniNumber", documentNumber);
        payer.put("billingAddress", address);

        Map<String, Object> card = new LinkedHashMap<>();
        card.put("number", cardNumber);
        card.put("securityCode", cvv);
        card.put("expirationDate", expirationDate);
        card.put("name", cardName);

        Map<String, Object> transaction = new LinkedHashMap<>();
        transaction.put("order", order);
        transaction.put("payer", payer);
        transaction.put("creditCard", card);
        transaction.put("extraParameters", Map.of("INSTALLMENTS_NUMBER", 1));
        transaction.put("type", "AUTHORIZATION_AND_CAPTURE");
        transaction.put("paymentMethod", paymentMethod);
        transaction.put("paymentCountry", "PE");
        transaction.put("deviceSessionId", UUID.randomUUID().toString().replace("-", ""));
        transaction.put("ipAddress", "127.0.0.1");
        transaction.put("cookie", "bruma-session");
        transaction.put("userAgent", "CinesBruma/1.0");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("language", "es");
        payload.put("command", "SUBMIT_TRANSACTION");
        payload.put("merchant", Map.of("apiKey", properties.getApiKey(), "apiLogin", properties.getApiLogin()));
        payload.put("transaction", transaction);
        payload.put("test", properties.isTest());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));

        String body;
        try {
            body = restTemplate.postForObject(properties.getUrl(), new HttpEntity<>(payload, headers), String.class);
        } catch (RestClientException exception) {
            log.error("PayU no respondio: {}", exception.getMessage());
            throw new ApiException(HttpStatus.BAD_GATEWAY, "No se pudo contactar al servicio de pagos");
        }

        return readCharge(body);
    }

    private PayuCharge readCharge(String body) {
        try {
            JsonNode root = objectMapper.readTree(body == null ? "{}" : body);
            if (!"SUCCESS".equals(root.path("code").asText())) {
                String error = root.path("error").asText("PayU rechazó la solicitud");
                log.warn("PayU devolvio error de solicitud");
                throw new ApiException(HttpStatus.BAD_GATEWAY, error);
            }
            JsonNode tx = root.path("transactionResponse");
            String state = tx.path("state").asText();
            String transactionId = tx.path("transactionId").asText("");
            log.info("PayU state={} transactionId={}", state, transactionId);
            if (!"APPROVED".equals(state)) {
                String message = tx.path("responseMessage").asText("");
                if (message.isBlank()) {
                    message = tx.path("responseCode").asText("El pago no fue aprobado");
                }
                throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, message);
            }
            JsonNode operationDate = tx.get("operationDate");
            String operation = operationDate == null || operationDate.isNull() ? "" : operationDate.asText();
            return new PayuCharge(transactionId, operation);
        } catch (ApiException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("Respuesta de PayU ilegible: {}", exception.getMessage());
            throw new ApiException(HttpStatus.BAD_GATEWAY, "La respuesta del servicio de pagos no se pudo leer");
        }
    }

    public static class PayuCharge {
        private final String transactionId;
        private final String operationDate;

        public PayuCharge(String transactionId, String operationDate) {
            this.transactionId = transactionId;
            this.operationDate = operationDate;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getOperationDate() {
            return operationDate;
        }
    }
}
