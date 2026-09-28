package com.cine.gateway.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> status(ResponseStatusException exception) {
        String message = exception.getReason() == null ? "No se pudo completar la operacion" : exception.getReason();
        return ResponseEntity.status(exception.getStatus()).body(body(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> invalid(MethodArgumentNotValidException exception) {
        String message = "Revisa los datos";
        if (exception.getBindingResult().getFieldError() != null
                && exception.getBindingResult().getFieldError().getDefaultMessage() != null) {
            message = exception.getBindingResult().getFieldError().getDefaultMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(message));
    }

    private Map<String, String> body(String message) {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("code", "1");
        payload.put("message", message);
        return payload;
    }
}
