package com.microservicio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.model.Telefono;
import com.microservicio.model.Usuario;
import com.microservicio.model.dto.RegistroRequest;
import com.microservicio.model.dto.UsuarioResponseDto;
import com.microservicio.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuarioMock;
    private RegistroRequest registroRequestMock;
    private UsuarioResponseDto usuarioResponseDtoMock;
    private Telefono telefonoMock;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        // 1. Configurar Telefono
        telefonoMock = Telefono.builder()
                .number("1234567")
                .citycode("1")
                .contrycode("57")
                .build();

        // 2. Configurar Usuario
        usuarioMock = Usuario.builder()
                .id(uuid)
                .username("juanperez")
                .email("juan@test.com")
                .password("PasswordStrong123")
                .phones(List.of(telefonoMock))
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .token("jwt-token-mock")
                .build();

        // 3. Configurar Request
        registroRequestMock = RegistroRequest.builder()
                .username("juanperez")
                .email("juan@test.com")
                .password("PasswordStrong123")
                .phones(List.of(telefonoMock))
                .build();

        // 4. Configurar Response
        usuarioResponseDtoMock = UsuarioResponseDto.builder()
                .id(uuid)
                .username("juanperez")
                .email("juan@test.com")
                .phones(List.of(telefonoMock))
                .created(LocalDateTime.now())
                .isActive(true)
                .token("jwt-token-mock")
                .build();
    }

    //region TESTS DE GET (Obtener todos)
    @Test
    @DisplayName("GET /api/v1/users - Retorna 200 y lista de usuarios")
    void getAllUsers_ShouldReturnList() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of(usuarioMock));

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("juanperez")));
    }

    @Test
    @DisplayName("GET /api/v1/users - Retorna 404 si la lista está vacía")
    void getAllUsers_WhenEmpty_ShouldReturn404() throws Exception {
        when(usuarioService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("No hay usuarios registrados")));
    }
    //endregion

    //region TESTS DE POST (Registro)
    @Test
    @DisplayName("POST /register - Crea usuario y retorna 201 Created")
    void registerUser_ShouldReturnCreated() throws Exception {
        when(usuarioService.registraUsuario(any(RegistroRequest.class))).thenReturn(usuarioResponseDtoMock);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequestMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(uuid.toString())))
                .andExpect(jsonPath("$.token", is("jwt-token-mock")));
    }
    //endregion

    //region TESTS DE GET BY ID
    @Test
    @DisplayName("GET /{id} - Retorna usuario por ID")
    void getUserById_ShouldReturnUser() throws Exception {
        when(usuarioService.findById(uuid)).thenReturn(usuarioMock);

        mockMvc.perform(get("/api/v1/users/{id}", uuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("juanperez")));
    }
    //endregion

    //region TESTS DE PUT (Actualizar completo)
    @Test
    @DisplayName("PUT /{id} - Actualiza usuario completo")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Usuario userUpdated = Usuario.builder()
                .id(uuid)
                .username("juan_updated")
                .password("temporal*123")
                .email("nuevo@test.com")
                .build();

        when(usuarioService.update(eq(uuid), any(Usuario.class))).thenReturn(userUpdated);

        mockMvc.perform(put("/api/v1/users/{id}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("juan_updated")));
    }
    //endregion

    //region TESTS DE PATCH (Actualizar parcial)
    @Test
    @DisplayName("PATCH /{id} - Actualiza usuario parcialmente")
    void patchUser_ShouldReturnPatchedUser() throws Exception {
        RegistroRequest patchReq = RegistroRequest.builder()
                .username("juan_patch")
                .build();

        Usuario userPatched = Usuario.builder()
                .id(uuid)
                .username("juan_patch")
                .email("juan@test.com")
                .build();

        when(usuarioService.patch(eq(uuid), any(RegistroRequest.class))).thenReturn(userPatched);

        mockMvc.perform(patch("/api/v1/users/{id}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("juan_patch")));
    }
    //endregion

    //region TESTS DE DELETE
    @Test
    @DisplayName("DELETE /{id} - Elimina usuario y retorna 204")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(usuarioService).delete(uuid);

        mockMvc.perform(delete("/api/v1/users/{id}", uuid))
                .andExpect(status().isNoContent());
    }
    //endregion
}