package com.cine.gateway.security;

import com.cine.gateway.api.LoginResponse;
import com.cine.gateway.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoogleAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AppProperties properties;
    private final JwtService jwtService;

    public GoogleAuthService(RestTemplate restTemplate, ObjectMapper objectMapper, AppProperties properties, JwtService jwtService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String idToken) {
        String clientId = properties.getGoogleClientId();
        if (clientId == null || clientId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El inicio con Google no está configurado");
        }

        String url = UriComponentsBuilder
                .fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
                .queryParam("id_token", idToken)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode profile;
        try {
            profile = objectMapper.readTree(response.getBody() == null ? "{}" : response.getBody());
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google no reconoció la sesión");
        }

        if (!response.getStatusCode().is2xxSuccessful() || profile.hasNonNull("error")) {
            log.warn("Google rechazo el inicio de sesion");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google no reconoció la sesión");
        }
        if (!clientId.equals(profile.path("aud").asText())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión de Google no corresponde a esta aplicación");
        }

        String email = profile.path("email").asText("").trim();
        String name = profile.path("name").asText("").trim();
        if (email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google no entregó el correo");
        }
        if (name.isBlank()) {
            name = email;
        }

        log.info("Sesion de Google aceptada para {}", email);
        return new LoginResponse(jwtService.issue(email, name), email, name);
    }
}
