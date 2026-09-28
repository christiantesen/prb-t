package com.cine.gateway.api;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Datos que el cliente envía para pagar la dulcería")
public class CheckoutForm {

    @NotEmpty
    @Valid
    @Schema(description = "Productos elegidos. Puede haber varios, del mismo tipo o de tipos distintos.", required = true)
    private List<Item> items = new ArrayList<>();

    @NotBlank
    @Email
    @Schema(example = "cliente@cine.com", description = "Correo del cliente. Si inició sesión, es el de su cuenta.", required = true)
    private String email;

    @NotBlank
    @Schema(example = "Lucía Mendoza", description = "Nombre del cliente. Si inició sesión, es el de su cuenta.", required = true)
    private String fullName;

    @NotBlank
    @Schema(example = "DNI", description = "Tipo de documento: DNI, CE o PAS", required = true)
    private String documentType;

    @NotBlank
    @Schema(example = "12345678", description = "Número de documento. Si el tipo es DNI, son 8 dígitos.", required = true)
    private String documentNumber;

    @NotBlank
    @Schema(example = "4907840000000005", description = "Número de tarjeta, 16 dígitos. No se guarda.", required = true)
    private String cardNumber;

    @NotBlank
    @Schema(example = "05/27", description = "Fecha de expiración en formato MM/AA", required = true)
    private String expirationDate;

    @NotBlank
    @Schema(example = "777", description = "CVV de 3 o 4 dígitos. No se guarda.", required = true)
    private String cvv;

    @Schema(example = "false", description = "En pruebas, márcalo para simular que PayU rechaza el pago")
    private boolean simulateRejection;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public boolean isSimulateRejection() {
        return simulateRejection;
    }

    public void setSimulateRejection(boolean simulateRejection) {
        this.simulateRejection = simulateRejection;
    }

    @Schema(description = "Un producto de la dulcería y cuántas unidades lleva")
    public static class Item {
        @NotNull
        @Schema(example = "1", description = "Identificador del producto", required = true)
        private Integer productId;

        @NotNull
        @Min(1)
        @Schema(example = "2", description = "Cantidad. Mínimo 1.", required = true)
        private Integer quantity;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
