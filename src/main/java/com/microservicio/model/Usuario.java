package com.microservicio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA que representa a un usuario en la base de datos.
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Basic
    @NotNull(message = "El campo de nombre de usuario no debe ser vacío")
    @Column(unique = true, name = "username")
    private String username;

    @Basic
    @NotNull(message = "El campo de email de usuario no debe ser vacío")
    @Column(unique = true, name = "email")
    private String email;

    @Basic
    @NotNull(message = "El campo de conrtaseña de usuario no debe ser vacío")
    @Column(name = "password")
    private String password;

    //Lista de Números de telefonos asociados
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<Telefono> phones;

    @Basic
    @Column(name = "created")
    private LocalDateTime created;

    @Basic
    @Column(name = "modified")
    private LocalDateTime modified;

    @Basic
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Basic
    @Column(name = "token")
    private String token;

    @Basic
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    public void onCreate() {
        this.created = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now(); // Same as created on first login
        this.modified = LocalDateTime.now();
        this.isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modified = LocalDateTime.now();
    }
}

