package com.cine.gateway.api;

import com.cine.gateway.security.GoogleAuthService;
import com.cine.gateway.security.JwtService;
import com.cine.gateway.user.UserAccount;
import com.cine.gateway.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@Tag(name = "Sesión", description = "Entrar con correo y clave, o con Google")
public class AuthController {
    private final UserRepository users;
    private final JwtService jwtService;
    private final GoogleAuthService googleAuthService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository users, JwtService jwtService, GoogleAuthService googleAuthService) {
        this.users = users;
        this.jwtService = jwtService;
        this.googleAuthService = googleAuthService;
    }

    @Operation(summary = "Entrar con correo y clave", description = "Devuelve el nombre y el correo para usarlos en la pantalla de pago, y una sesión.")
    @PostMapping("/api/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        UserAccount user = users.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos"));
        if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos");
        }
        String token = jwtService.issue(user.getEmail(), user.getFullName());
        return new LoginResponse(token, user.getEmail(), user.getFullName());
    }

    @Operation(summary = "Entrar con Google", description = "Recibe la credencial del botón de Google, comprueba que sea de esta aplicación y devuelve el nombre y el correo para la pantalla de pago.")
    @PostMapping("/api/auth/google")
    public LoginResponse google(@Valid @RequestBody GoogleLoginRequest request) {
        return googleAuthService.login(request.getIdToken());
    }
}
