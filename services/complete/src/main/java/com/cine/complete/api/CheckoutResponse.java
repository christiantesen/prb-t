package com.cine.complete.api;

import java.math.BigDecimal;

public class CheckoutResponse {

    private String code;
    private String transactionId;
    private String operationDate;
    private BigDecimal total;
    private String message;

    public CheckoutResponse(String code, String transactionId, String operationDate, BigDecimal total, String message) {
        this.code = code;
        this.transactionId = transactionId;
        this.operationDate = operationDate;
        this.total = total;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getMessage() {
        return message;
    }
}
