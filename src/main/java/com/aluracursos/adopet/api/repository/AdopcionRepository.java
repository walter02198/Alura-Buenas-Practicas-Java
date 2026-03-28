package com.aluracursos.adopet.api.repository;

import com.aluracursos.adopet.api.model.Adopcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdopcionRepository extends JpaRepository<Adopcion, Long> {

}
