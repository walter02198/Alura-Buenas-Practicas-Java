package com.aluracursos.adopet.api.controller;

import com.aluracursos.adopet.api.model.Tutor;
import com.aluracursos.adopet.api.repository.TutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    @Autowired
    private TutorRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<String> registrar(@RequestBody @Valid Tutor tutor) {
        boolean telefonoYaRegistrado = repository.existsByTelefono(tutor.getTelefono());
        boolean emailYaRegistrado = repository.existsByEmail(tutor.getEmail());

        if (telefonoYaRegistrado || emailYaRegistrado) {
            return ResponseEntity.badRequest().body("Datos ya registrados para otro tutor!");
        } else {
            repository.save(tutor);
            return ResponseEntity.ok().build();
        }
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> actualizar(@RequestBody @Valid Tutor tutor) {
        repository.save(tutor);
        return ResponseEntity.ok().build();
    }

}
