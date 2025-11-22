package com.microservicio.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservicio.model.Telefono;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponseDto {
    private UUID id;
    private String username;
    private String email;
    List<Telefono> phones;
    private LocalDateTime created;
    private LocalDateTime modified;
    private LocalDateTime lastLogin;
    private String token;
    private Boolean isActive;
}
