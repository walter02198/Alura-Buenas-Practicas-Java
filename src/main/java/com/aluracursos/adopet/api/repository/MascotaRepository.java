package com.aluracursos.adopet.api.repository;

import com.aluracursos.adopet.api.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

}
