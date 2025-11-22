package com.microservicio.controller;

import com.microservicio.model.Usuario;
import com.microservicio.model.dto.RegistroRequest;
import com.microservicio.model.dto.UsuarioResponseDto;
import com.microservicio.service.UsuarioService;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Gestión de Usuarios", description = "Operaciones para registrar, buscar y administrar usuarios")
public class UserController {

    private final UsuarioService oServicio;

    @Autowired
    public UserController(UsuarioService oServicio) {
        this.oServicio = oServicio;
    }

    //region OBTENER TODOS LOS REGISTROS
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista de todos los usuarios registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios registrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"mensaje\": \"No hay usuarios registrados\"}")))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers() {
        List<Usuario> users = oServicio.findAll();

        if (users.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "No hay usuarios registrados"));
        }

        return ResponseEntity.ok(users);
    }
    //endregion

    //region REGISTRAR USUARIO
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un usuario, valida el formato de correo/contraseña, verifica duplicados y genera token/UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación (Password débil o Email inválido)",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"mensaje\": \"La contraseña no cumple con el formato...\"}"))),
            @ApiResponse(responseCode = "409", description = "Conflicto: El correo ya está registrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"mensaje\": \"El correo ya registrado\"}")))
    })
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> registerUser(
            @Parameter(description = "Datos necesarios para el registro", required = true)
            @Valid @RequestBody RegistroRequest usuario) throws Exception {
        UsuarioResponseDto usuarioCreado = oServicio.registraUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }
    //endregion

    //region BUSCAR USUARIO POR ID
    @Operation(summary = "Obtener usuario por ID", description = "Retorna el detalle completo de un usuario específico buscando por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"mensaje\": \"Usuario no encontrado.\"}")))
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserById(
            @Parameter(description = "Identificador único del usuario (UUID)", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", required = true)
            @PathVariable UUID id) throws Exception {
        Usuario user = oServicio.findById(id);
        return ResponseEntity.ok(user);
    }
    //endregion

    //region ACTUALIZAR USUARIO COMPLETO
    @Operation(summary = "Actualizar usuario completo (PUT)", description = "Reemplaza todos los datos del usuario. Requiere enviar todos los campos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"mensaje\": \"Usuario no encontrado.\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(
            @Parameter(description = "UUID del usuario a actualizar", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Objeto usuario con los nuevos datos", required = true)
            @Valid @RequestBody Usuario userDetails) throws Exception {
        Usuario updatedUser = oServicio.update(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
    //endregion

    //region ACTUALIZAR DATOS PARCIALES
    @Operation(summary = "Actualización parcial (PATCH)", description = "Actualiza solo los campos enviados (nombre, email o password) sin modificar el resto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> patchUser(
            @Parameter(description = "UUID del usuario", required = true)
            @PathVariable UUID id,
            @Parameter(description = "DTO con los campos que se desean cambiar", required = true)
            @RequestBody RegistroRequest userDetails) throws Exception {
        Usuario updatedUser = oServicio.patch(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
    //endregion

    //region ELIMINAR USUARIO
    @Operation(summary = "Eliminar usuario", description = "Elimina físicamente un usuario de la base de datos por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID del usuario a eliminar", required = true)
            @Valid @PathVariable UUID id) throws Exception {
        oServicio.delete(id);
        return ResponseEntity.noContent().build();
    }
    //endregion
}