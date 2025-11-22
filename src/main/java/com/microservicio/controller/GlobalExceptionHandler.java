package com.microservicio.controller;

import com.microservicio.exception.EmailException;
import com.microservicio.exception.PasswordException;
import com.microservicio.exception.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Captura tus excepciones personalizadas de Conflicto de Usuario Existente
    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserExists(UserExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("mensaje", e.getMessage()));
    }

    //  Captura errores de validación de Spring
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String mensaje = error.getDefaultMessage();
            errors.put("mensaje", mensaje);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    //Captira Errores Generales de contraseña o Email
    @ExceptionHandler({EmailException.class, PasswordException.class})
    public ResponseEntity<Map<String, String>> handleBadRequests(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("mensaje", e.getMessage()));
    }


    // Captura Cualquier otro error no previsto como NullPointer
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("mensaje", "Error interno del servidor: " + e.getMessage()));
    }
}
