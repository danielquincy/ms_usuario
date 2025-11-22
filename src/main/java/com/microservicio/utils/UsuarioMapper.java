package com.microservicio.utils;

import com.microservicio.model.Usuario;
import com.microservicio.model.dto.UsuarioResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDto toDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPhones(),
                usuario.getCreated(),
                usuario.getModified(),
                usuario.getLastLogin(),
                usuario.getToken(),
                usuario.getIsActive());
    }
}
