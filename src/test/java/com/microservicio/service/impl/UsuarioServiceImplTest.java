package com.microservicio.service.impl;

import com.microservicio.exception.EmailException;
import com.microservicio.exception.PasswordException;
import com.microservicio.exception.UserExistsException;
import com.microservicio.model.Telefono;
import com.microservicio.model.Usuario;
import com.microservicio.model.dto.RegistroRequest;
import com.microservicio.model.dto.UsuarioResponseDto;
import com.microservicio.repository.TelefonoDao;
import com.microservicio.repository.UsuarioDao;
import com.microservicio.utils.JwtUtil;
import com.microservicio.utils.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils; // Para inyectar @Value

import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    // Dependencias a mockear
    @Mock
    private UsuarioDao oUsuarioDao;
    @Mock
    private TelefonoDao oTelefonoDao;
    @Mock
    private PasswordEncoder oPass;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UsuarioMapper mapper;

    // La clase a probar (inyección de mocks)
    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    // Dependencias configuradas manualmente (patrones)
    private Pattern emailPattern;
    private Pattern passwordPattern;

    // Datos de prueba
    private RegistroRequest registroRequest;
    private Usuario usuario ;
    private final UUID TEST_UUID = UUID.fromString("8d9f7064-61d7-4a27-b615-002d4ef3f24b");
    private final String VALID_USERNAME = "test";
    private final String VALID_EMAIL = "test@dominio.cl";
    private final String VALID_PASS = "Pass123456"; // Debe coincidir con el regex
    private final String oEmail = "nuevo@dominio.cl"; // Debe coincidir con el regex

    @BeforeEach
    void setUp() {
        // Configurar los Patterns que el constructor necesita
        emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

        // Inyectar manualmente las dependencias de Pattern y la dependencia @Value
        ReflectionTestUtils.setField(usuarioService, "emailPattern", emailPattern);

        // El regex que se asume en tu código (debe coincidir con la variable @Value)
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        passwordPattern = Pattern.compile(passwordRegex);
        ReflectionTestUtils.setField(usuarioService, "passwordRegex", passwordRegex);

        // Crear objeto de prueba
        registroRequest = new RegistroRequest();
        registroRequest.setUsername("testuser");
        registroRequest.setEmail(VALID_EMAIL);
        registroRequest.setPassword(VALID_PASS);
        registroRequest.setPhones(List.of(new Telefono(null, "88654321", "55", "505")));

        usuario = Usuario.builder()
                .id(TEST_UUID)
                .username("testuser")
                .email(VALID_EMAIL)
                .password("encoded_pass")
                .token("jwt_token")
                .phones(registroRequest.getPhones())
                .build();
    }

    // --- PRUEBAS DE REGISTRO DE USUARIO ---

    @Test
    @DisplayName("Debe registrar un usuario exitosamente y retornar DTO")
    void registraUsuario_Exito() throws Exception {
        // Arrange
        UsuarioResponseDto expectedDto = UsuarioResponseDto.builder().id(TEST_UUID).build();

        // Mocks de validación: No existe username, no existe email
        when(oUsuarioDao.findByUsername(anyString())).thenReturn(Optional.empty());
        when(oUsuarioDao.findAll()).thenReturn(Collections.emptyList());

        // Mocks de seguridad y mapeo
        when(jwtUtil.generarToken(any(RegistroRequest.class))).thenReturn("jwt_token");
        when(oPass.encode(anyString())).thenReturn("encoded_pass");
        when(oUsuarioDao.save(any(Usuario.class))).thenReturn(usuario);
        when(mapper.toDto(any(Usuario.class))).thenReturn(expectedDto);

        // Act
        UsuarioResponseDto result = usuarioService.registraUsuario(registroRequest);

        // Assert
        assertThat(result).isEqualTo(expectedDto);
        verify(oUsuarioDao, times(1)).save(any(Usuario.class));
        verify(oTelefonoDao, times(1)).save(any(Telefono.class)); // Se llama 1 vez por teléfono en la lista
    }

    @Test
    @DisplayName("Debe lanzar UserExistsException si el username ya existe")
    void registraUsuario_UsernameYaExiste_LanzaExcepcion() {
        // Arrange
        when(oUsuarioDao.findByUsername(anyString())).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registraUsuario(registroRequest))
                .isInstanceOf(UserExistsException.class)
                .hasMessageContaining("El nombre de usuario ya existe.");
        verify(oUsuarioDao, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar EmailException si el email es inválido")
    void registraUsuario_EmailInvalido_LanzaExcepcion() {
        // Arrange
        registroRequest.setEmail("email_invalido");
        when(oUsuarioDao.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registraUsuario(registroRequest))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("Email inválido: El formato debe ser tu_correo@dominio.cl");
        verify(oUsuarioDao, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar EmailException si el correo ya está registrado")
    void registraUsuario_EmailYaRegistrado_LanzaExcepcion() {
        // Arrange
        when(oUsuarioDao.findByUsername(anyString())).thenReturn(Optional.empty());

        // Usuario existente con el mismo email
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail(VALID_EMAIL.toUpperCase());
        when(oUsuarioDao.findAll()).thenReturn(List.of(usuarioExistente));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registraUsuario(registroRequest))
                .isInstanceOf(EmailException.class)
                .hasMessageContaining("El correo ingresado ya se encuentra registrado.");
        verify(oUsuarioDao, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar PasswordException si la contraseña no cumple el formato")
    void registraUsuario_PasswordInvalida_LanzaExcepcion() {
        // Arrange
        registroRequest.setPassword("short"); // No cumple con el regex
        when(oUsuarioDao.findByUsername(anyString())).thenReturn(Optional.empty());
        when(oUsuarioDao.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registraUsuario(registroRequest))
                .isInstanceOf(PasswordException.class)
                .hasMessageContaining("La contraseña no cumple con el formato correcto");
        verify(oUsuarioDao, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("validarEmail debe retornar true para un email válido")
    void validarEmail_Valido_RetornaTrue() {
        assertTrue(usuarioService.validarEmail(VALID_EMAIL));
    }

    @Test
    @DisplayName("validarEmail debe retornar false para un email inválido")
    void validarEmail_Invalido_RetornaFalse() {
        assertFalse(usuarioService.validarEmail("mal_formato.com"));
    }

    @Test
    @DisplayName("delete debe eliminar un usuario existente")
    void delete_UsuarioExiste_Elimina() throws Exception {
        // Arrange
        Usuario usuarioExistente = Usuario.builder()
                .id(TEST_UUID)
                .username(VALID_USERNAME)
                .email("correo@dominio.cl")
                .build();

        when(oUsuarioDao.findAll())
                .thenReturn(List.of(usuarioExistente)); // Devuelve una lista con el usuario

        // Act
        usuarioService.delete(TEST_UUID);

        // Assert
        // Verificar que se llamó a findAll() (para obtener la lista y luego buscar en ella)
        verify(oUsuarioDao, times(1)).findAll();
        // Verificar que se llamó a delete() con el objeto Usuario
        verify(oUsuarioDao, times(1)).delete(usuarioExistente);
    }

    @Test
    @DisplayName("delete debe lanzar UserExistsException si el usuario no existe")
    void delete_UsuarioNoExiste_LanzaExcepcion() {
        // Arrange

        when(oUsuarioDao.findAll()).thenReturn(List.of()); // O Collections.emptyList()

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.delete(TEST_UUID))
                .isInstanceOf(UserExistsException.class)
                .hasMessageContaining("Usuario no encontrado");

        // Assert
        // Verificar que se llamó a findAll() (para buscar el usuario)
        verify(oUsuarioDao, times(1)).findAll();
        // Verificar que NUNCA se llamó a delete()
        verify(oUsuarioDao, never()).delete(any(Usuario.class));
    }
}