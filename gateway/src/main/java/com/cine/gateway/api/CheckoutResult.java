package com.cine.gateway.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resultado cuando el pago fue aprobado y la compra quedó registrada")
public class CheckoutResult {

    @Schema(example = "0", description = "0 significa compra correcta")
    private String code;

    @Schema(example = "d3ab9123-0a70-4d4c-9ba6-d685bad76c58", description = "Identificador de la transacción que devolvió PayU")
    private String transactionId;

    @Schema(example = "1790529215855", description = "Fecha de operación que devolvió PayU")
    private String operationDate;

    @Schema(example = "14.50", description = "Total cobrado, calculado con los precios de la dulcería")
    private BigDecimal total;

    @Schema(example = "Compra correcta")
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
