package com.aluracursos.adopet.api.controller;

import com.aluracursos.adopet.api.model.Mascota;
import com.aluracursos.adopet.api.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaRepository repository;

    @GetMapping
    public ResponseEntity<List<Mascota>> listarTodasDisponibles() {
        List<Mascota> mascotas = repository.findAll();
        List<Mascota> disponibles = new ArrayList<>();
        for (Mascota mascota : mascotas) {
            if (mascota.getAdoptada() == false) {
                disponibles.add(mascota);
            }
        }
        return ResponseEntity.ok(disponibles);
    }

}
