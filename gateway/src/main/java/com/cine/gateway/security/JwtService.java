package com.cine.gateway.security;

import com.cine.gateway.config.AppProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final AppProperties properties;

    public JwtService(AppProperties properties) {
        this.properties = properties;
    }

    public String issue(String email, String fullName) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("name", fullName)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(2 * 60 * 60)))
                .signWith(Keys.hmacShaKeyFor(secret()), SignatureAlgorithm.HS256)
                .compact();
    }

    public void require(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesion no es valida");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret()))
                    .build()
                    .parseClaimsJws(token);
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesion expiro o no es valida");
        }
    }

    private byte[] secret() {
        return properties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
    }
}
