package com.cine.gateway.api;

import com.cine.gateway.config.AppProperties;
import com.cine.gateway.security.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Tag(name = "Tienda", description = "Cartelera, dulcería y pago")
public class ProxyController {
    private static final Logger log = LoggerFactory.getLogger(ProxyController.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AppProperties properties;
    private final JwtService jwtService;

    public ProxyController(RestTemplate restTemplate, ObjectMapper objectMapper, AppProperties properties, JwtService jwtService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.jwtService = jwtService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", "gateway");
        return body;
    }

    @GetMapping("/api/premieres")
    public ResponseEntity<String> premieres() {
        return forward(properties.getPremieresUrl() + "/premieres", HttpMethod.GET, null);
    }

    @GetMapping("/api/candy")
    public ResponseEntity<String> candy() {
        return forward(properties.getCandyUrl() + "/candy", HttpMethod.GET, null);
    }

    @Operation(
            summary = "Pagar la dulcería",
            description = "Envía la tarjeta al sandbox de PayU. Si el pago se aprueba, guarda la compra y responde con el código 0, el identificador de la transacción y la fecha de operación que devolvió PayU. El número de tarjeta y el CVV no se guardan."
    )
    @ApiResponse(responseCode = "200", description = "Compra correcta",
            content = @Content(schema = @Schema(implementation = CheckoutResult.class)))
    @ApiResponse(responseCode = "422", description = "PayU no aprobó el pago")
    @ApiResponse(responseCode = "400", description = "Falta un dato o el dato no es válido")
    @PostMapping("/api/checkout")
    public ResponseEntity<String> checkout(
            @Valid @RequestBody CheckoutForm form,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && !authorization.isBlank()) {
            jwtService.require(authorization);
        }
        String body;
        try {
            body = objectMapper.writeValueAsString(form);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo preparar el pago", exception);
        }
        return forward(properties.getCompleteUrl() + "/checkout", HttpMethod.POST, body);
    }

    private ResponseEntity<String> forward(String url, HttpMethod method, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestId = MDC.get("requestId");
        if (requestId != null) {
            headers.set("X-Request-Id", requestId);
        }
        log.info("{} {}", method, url);
        ResponseEntity<String> response = restTemplate.exchange(url, method, new HttpEntity<>(body, headers), String.class);
        MediaType type = response.getHeaders().getContentType();
        return ResponseEntity.status(response.getStatusCode())
                .contentType(type == null ? MediaType.APPLICATION_JSON : type)
                .body(response.getBody());
    }
}
