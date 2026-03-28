package com.aluracursos.adopet.api.repository;

import com.aluracursos.adopet.api.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    boolean existsByTelefono(String telefono);

    boolean existsByEmail(String email);

}
