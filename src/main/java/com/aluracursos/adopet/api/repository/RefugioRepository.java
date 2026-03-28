package com.aluracursos.adopet.api.repository;

import com.aluracursos.adopet.api.model.Refugio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefugioRepository extends JpaRepository<Refugio, Long> {
    boolean existsByNombre(String nombre);

    boolean existsByTelefono(String telefono);

    boolean existsByEmail(String email);

    Refugio findByNombre(String nombre);
}
