package com.microservicio.service;

import com.microservicio.exception.UserExistsException;
import com.microservicio.model.Usuario;
import com.microservicio.model.dto.RegistroRequest;
import com.microservicio.model.dto.UsuarioResponseDto;

import java.util.List;
import java.util.UUID;

public interface UsuarioService {

    Boolean validarEmail(String email);

    UsuarioResponseDto registraUsuario(RegistroRequest prUsuario) throws Exception;

    List<Usuario> findAll();

    Usuario update(UUID id, Usuario prUsuario)throws Exception ;

    Usuario patch(UUID id, RegistroRequest userDetails) throws Exception;

    void delete(UUID id)throws Exception;

    Usuario findById(UUID id) throws UserExistsException;
}
