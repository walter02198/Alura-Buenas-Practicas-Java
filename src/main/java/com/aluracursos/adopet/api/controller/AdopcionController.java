package com.aluracursos.adopet.api.controller;

import com.aluracursos.adopet.api.exception.ValidacionException;
import com.aluracursos.adopet.api.model.Adopcion;
import com.aluracursos.adopet.api.model.StatusAdopcion;
import com.aluracursos.adopet.api.repository.AdopcionRepository;
import com.aluracursos.adopet.api.service.AdopcionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/adopciones")
public class AdopcionController {

    @Autowired
    private AdopcionService adopcionService;


    @PostMapping
    @Transactional

    public ResponseEntity<String> solicitar(@RequestBody @Valid Adopcion adopcion) {
        try{

            this.adopcionService.solicitar(adopcion);
            return ResponseEntity.ok().build();
        } catch (ValidacionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PutMapping("/aprobar")
    @Transactional
    public ResponseEntity<String> aprovar(@RequestBody @Valid Adopcion adopcion) {
        adopcion.setStatus(StatusAdopcion.APROBADO);
        repository.save(adopcion);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("adopet@email.com");
        email.setTo(adopcion.getTutor().getEmail());
        email.setSubject("Adopción aprobada");
        email.setText("Felicitaciones " + adopcion.getTutor().getNombre() + "!\n\nSu adopción de la mascota " + adopcion.getMascota().getNombre() + ", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", fue aprobada.\nPor favor, entrar en contacto con el refugio " + adopcion.getMascota().getRefugio().getNombre() + " para ir a buscar a su mascota.");
        emailSender.send(email);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/reprobar")
    @Transactional
    public ResponseEntity<String> reprobar(@RequestBody @Valid Adopcion adopcion) {
        adopcion.setStatus(StatusAdopcion.REPROBADO);
        repository.save(adopcion);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("adopet@email.com");
        email.setTo(adopcion.getTutor().getEmail());
        email.setSubject("Adopción reprobada");
        email.setText("Hola " + adopcion.getTutor().getNombre() + "!\n\nInfelizmente su adopción de la mascota " + adopcion.getMascota().getNombre() + ", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", fue reprobada por el refugio " + adopcion.getMascota().getRefugio().getNombre() + " con la seguiente justificativa: " + adopcion.getJustificativaStatus());
        emailSender.send(email);

        return ResponseEntity.ok().build();
    }

}
