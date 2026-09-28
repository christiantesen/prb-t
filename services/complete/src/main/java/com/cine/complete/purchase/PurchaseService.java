package com.cine.complete.purchase;

import com.cine.complete.api.ApiException;
import com.cine.complete.api.CheckoutRequest;
import com.cine.complete.api.CheckoutResponse;
import com.cine.complete.payu.PayuClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);
    private static final Pattern EXPIRY = Pattern.compile("^(0[1-9]|1[0-2])/(\\d{2})$");
    private static final Pattern CVV = Pattern.compile("^\\d{3,4}$");

    private final PurchaseRepository repository;
    private final PayuClient payuClient;

    public PurchaseService(PurchaseRepository repository, PayuClient payuClient) {
        this.repository = repository;
        this.payuClient = payuClient;
    }

    public CheckoutResponse checkout(CheckoutRequest request) {
        String documentType = request.getDocumentType().trim().toUpperCase();
        String documentNumber = request.getDocumentNumber().trim();
        validateDocument(documentType, documentNumber);

        String cardNumber = request.getCardNumber().replaceAll("\\s+", "");
        if (!cardNumber.matches("\\d{16}") || !luhn(cardNumber)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El número de tarjeta debe tener 16 dígitos válidos");
        }
        if (!CVV.matcher(request.getCvv().trim()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El CVV debe tener 3 o 4 dígitos");
        }
        String expiration = toPayuExpiry(request.getExpirationDate().trim());
        String paymentMethod = paymentMethod(cardNumber);

        Map<Integer, CandyProduct> catalog = new LinkedHashMap<>();
        for (CandyProduct product : repository.listCandy()) {
            catalog.put(product.getId(), product);
        }

        List<Map<String, Object>> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CheckoutRequest.Item item : request.getItems()) {
            CandyProduct product = catalog.get(item.getProductId());
            if (product == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Hay un producto que ya no está en dulcería");
            }
            if (item.getQuantity() > 20) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "La cantidad máxima por producto es 20");
            }
            BigDecimal line = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(line);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("productId", product.getId());
            row.put("productName", product.getName());
            row.put("quantity", item.getQuantity());
            row.put("unitPrice", product.getPrice());
            lines.add(row);
        }
        total = total.setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El total de la compra debe ser mayor a cero");
        }

        PayuClient.PayuCharge charge = payuClient.charge(
                total,
                request.getFullName().trim(),
                request.getEmail().trim(),
                documentNumber,
                cardNumber,
                expiration,
                request.getCvv().trim(),
                paymentMethod,
                request.isSimulateRejection());

        String code = repository.complete(
                request.getEmail().trim(),
                request.getFullName().trim(),
                documentType,
                documentNumber,
                charge.getOperationDate(),
                charge.getTransactionId(),
                total,
                lines);
        if (!"0".equals(code)) {
            log.error("sp_complete_purchase devolvio {}", code);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "El pago se aprobó, pero no se pudo registrar la compra");
        }

        log.info("Compra registrada transactionId={}", charge.getTransactionId());
        return new CheckoutResponse("0", charge.getTransactionId(), charge.getOperationDate(), total, "Compra correcta");
    }

    private void validateDocument(String type, String number) {
        boolean valid;
        switch (type) {
            case "DNI":
                valid = number.matches("\\d{8}");
                break;
            case "CE":
            case "PAS":
                valid = number.matches("[A-Za-z0-9]{6,12}");
                break;
            default:
                throw new ApiException(HttpStatus.BAD_REQUEST, "El tipo de documento debe ser DNI, CE o PAS");
        }
        if (!valid) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El número de documento no corresponde al tipo elegido");
        }
    }

    private String toPayuExpiry(String value) {
        java.util.regex.Matcher matcher = EXPIRY.matcher(value);
        if (!matcher.matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La expiración debe tener el formato MM/AA");
        }
        int month = Integer.parseInt(matcher.group(1));
        int year = 2000 + Integer.parseInt(matcher.group(2));
        YearMonth expiry = YearMonth.of(year, month);
        if (expiry.isBefore(YearMonth.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La tarjeta está vencida");
        }
        return year + "/" + matcher.group(1);
    }

    private String paymentMethod(String digits) {
        if (digits.startsWith("4")) {
            return "VISA";
        }
        if (digits.startsWith("5")) {
            return "MASTERCARD";
        }
        if (digits.startsWith("34") || digits.startsWith("37")) {
            return "AMEX";
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Solo se aceptan Visa, Mastercard y American Express");
    }

    private boolean luhn(String digits) {
        int sum = 0;
        boolean alternate = false;
        for (int index = digits.length() - 1; index >= 0; index--) {
            int digit = digits.charAt(index) - '0';
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
}
