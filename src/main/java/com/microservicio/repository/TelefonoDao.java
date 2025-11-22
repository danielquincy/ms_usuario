package com.microservicio.repository;

import com.microservicio.model.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TelefonoDao extends JpaRepository<Telefono, UUID> {
}
