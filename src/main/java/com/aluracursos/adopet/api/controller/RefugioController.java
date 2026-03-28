package com.aluracursos.adopet.api.controller;

import com.aluracursos.adopet.api.model.Mascota;
import com.aluracursos.adopet.api.model.Refugio;
import com.aluracursos.adopet.api.repository.RefugioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refugios")
public class RefugioController {

    @Autowired
    private RefugioRepository repository;

    @GetMapping
    public ResponseEntity<List<Refugio>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<String> registrar(@RequestBody @Valid Refugio refugio) {
        boolean nombreYaRegistrado = repository.existsByNombre(refugio.getNombre());
        boolean telefonoYaRegistrado = repository.existsByTelefono(refugio.getTelefono());
        boolean emailYaRegistrado = repository.existsByEmail(refugio.getEmail());

        if (nombreYaRegistrado || telefonoYaRegistrado || emailYaRegistrado) {
            return ResponseEntity.badRequest().body("Datos ya registrados para otro refugio!");
        } else {
            repository.save(refugio);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/{idONombre}/mascotas")
    public ResponseEntity<List<Mascota>> listarMascotas(@PathVariable String idONombre) {
        try {
            Long id = Long.parseLong(idONombre);
            List<Mascota> mascotas = repository.getReferenceById(id).getMascotas();
            return ResponseEntity.ok(mascotas);
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.notFound().build();
        } catch (NumberFormatException e) {
            try {
                List<Mascota> mascotas = repository.findByNombre(idONombre).getMascotas();
                return ResponseEntity.ok(mascotas);
            } catch (EntityNotFoundException enfe) {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PostMapping("/{idONombre}/mascotas")
    @Transactional
    public ResponseEntity<String> registrarMascota(@PathVariable String idONombre, @RequestBody @Valid Mascota mascota) {
        try {
            Long id = Long.parseLong(idONombre);
            Refugio refugio = repository.getReferenceById(id);
            mascota.setRefugio(refugio);
            mascota.setAdoptada(false);
            refugio.getMascotas().add(mascota);
            repository.save(refugio);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.notFound().build();
        } catch (NumberFormatException nfe) {
            try {
                Refugio refugio = repository.findByNombre(idONombre);
                mascota.setRefugio(refugio);
                mascota.setAdoptada(false);
                refugio.getMascotas().add(mascota);
                repository.save(refugio);
                return ResponseEntity.ok().build();
            } catch (EntityNotFoundException enfe) {
                return ResponseEntity.notFound().build();
            }
        }
    }

}
