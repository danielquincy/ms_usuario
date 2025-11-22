package com.microservicio.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "telefono")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Telefono {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "citycode",nullable = false)
    private String citycode;

    @Column(name = "contrycode",nullable = false)
    private String contrycode;

}