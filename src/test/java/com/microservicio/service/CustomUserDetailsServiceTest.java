package com.microservicio.service;

import com.microservicio.model.Usuario;
import com.microservicio.repository.UsuarioDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest { // Asume que tienes una clase con este nombre

    // 1. Mock de la dependencia (el repositorio)
    @Mock
    private UsuarioDao usuarioDao;

    // 2. Inyectar los mocks en la clase a probar
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Usuario usuario;
    private final String TEST_USERNAME = "juan123";
    private final String NON_EXISTENT_USERNAME = "inexistente";

    @BeforeEach
    void setUp() {
        // Objeto de usuario para simular la respuesta de la base de datos
        usuario = new Usuario();
        usuario.setUsername(TEST_USERNAME);
        usuario.setPassword("pass-encriptada-123");
    }

    // --- CASOS DE ÉXITO ---

    @Test
    @DisplayName("Debe cargar el usuario exitosamente cuando el username existe")
    void loadUserByUsername_Exito() {
        // --- ARRANGE ---
        // Simulamos que SÍ encuentra el usuario
        when(usuarioDao.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(usuario));

        // --- ACT ---
        UserDetails result = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

        // --- ASSERT ---
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(result.getPassword()).isEqualTo("pass-encriptada-123");
        assertThat(result.getAuthorities()).isEmpty();
    }

    // --- CASOS DE FALLO ---

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException cuando el usuario NO existe")
    void loadUserByUsername_NoEncontrado() {
        // --- ARRANGE ---
        // Simulamos que NO encuentra nada (Optional vacío)
        when(usuarioDao.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        // Spring Security requiere que se lance exactamente esta excepción
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(NON_EXISTENT_USERNAME))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado"); // Debe coincidir con el mensaje de tu servicio
    }
}