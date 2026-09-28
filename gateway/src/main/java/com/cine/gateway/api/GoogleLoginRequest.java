package com.cine.gateway.api;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "Credencial que entrega el botón de Google en el navegador")
public class GoogleLoginRequest {

    @NotBlank
    @Schema(description = "Token de identidad de Google. El servidor comprueba que sea de esta aplicación y lee el correo y el nombre.", required = true)
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
