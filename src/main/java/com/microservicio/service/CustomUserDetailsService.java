package com.microservicio.service;

import com.microservicio.model.Usuario;
import com.microservicio.repository.UsuarioDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioDao dao;

    @Autowired
    public CustomUserDetailsService(UsuarioDao dao) {
        this.dao = dao;
    }

    /**
     * Carga el usuario por nombre de usuario para el proceso de autenticación.
     *
     * @param username El nombre de usuario proporcionado en la autenticación Basic Auth.
     * @return UserDetails que contiene el nombre de usuario, la contraseña hasheada y las autoridades.
     * @throws UsernameNotFoundException Si el usuario no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws AccessDeniedException {

        // 1. Buscar el usuario en la base de datos
        Usuario user = dao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        //2. Crear un objeto UserDetails de Spring Security usando la información del usuario
        return new User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

}
