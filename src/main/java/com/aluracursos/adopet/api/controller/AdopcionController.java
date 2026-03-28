package com.aluracursos.adopet.api.controller;

import com.aluracursos.adopet.api.model.Adopcion;
import com.aluracursos.adopet.api.model.StatusAdopcion;
import com.aluracursos.adopet.api.repository.AdopcionRepository;
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
    private AdopcionRepository repository;

    @Autowired
    private JavaMailSender emailSender;

    @PostMapping
    @Transactional
    public ResponseEntity<String> solicitar(@RequestBody @Valid Adopcion adopcion) {
        if (adopcion.getMascota().getAdoptada() == true) {
            return ResponseEntity.badRequest().body("Mascota ya fue adoptada!");
        } else {
            List<Adopcion> adopciones = repository.findAll();
            for (Adopcion a : adopciones) {
                if (a.getTutor() == adopcion.getTutor() && a.getStatus() == StatusAdopcion.ESPERANDO_EVALUACION) {
                    return ResponseEntity.badRequest().body("Tutor ya tiene otra adopción esperando evaluación!");
                }
            }
            for (Adopcion a : adopciones) {
                if (a.getMascota() == adopcion.getMascota() && a.getStatus() == StatusAdopcion.ESPERANDO_EVALUACION) {
                    return ResponseEntity.badRequest().body("Mascota ya esta esperando evaluación para ser adoptada!");
                }
            }
            for (Adopcion a : adopciones) {
                int contador = 0;
                if (a.getTutor() == adopcion.getTutor() && a.getStatus() == StatusAdopcion.APROBADO) {
                    contador = contador + 1;
                }
                if (contador == 5) {
                    return ResponseEntity.badRequest().body("Tutor llegó al limite máximo de 5 adopciones!");
                }
            }
        }
        adopcion.setFecha(LocalDateTime.now());
        adopcion.setStatus(StatusAdopcion.ESPERANDO_EVALUACION);
        repository.save(adopcion);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("adopet@email.com");
        email.setTo(adopcion.getMascota().getRefugio().getEmail());
        email.setSubject("Solicitación de adopción");
        email.setText("Hola " + adopcion.getMascota().getRefugio().getNombre() +"!\n\nUna solicitud de adopción fue registrada hoy para la mascota: " + adopcion.getMascota().getNombre() +". \nPor favor, evaluarla para aprobación o reprobación.");
        emailSender.send(email);

        return ResponseEntity.ok().build();
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
        email.setText("Felicitaciones " + adopcion.getTutor().getNombre() +"!\n\nSu adopción de la mascota " + adopcion.getMascota().getNombre() +", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +", fue aprobada.\nPor favor, entrar en contacto con el refugio " + adopcion.getMascota().getRefugio().getNombre() +" para ir a buscar a su mascota.");
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
        email.setText("Hola " + adopcion.getTutor().getNombre() +"!\n\nInfelizmente su adopción de la mascota " + adopcion.getMascota().getNombre() +", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +", fue reprobada por el refugio " + adopcion.getMascota().getRefugio().getNombre() +" con la seguiente justificativa: " + adopcion.getJustificativaStatus());
        emailSender.send(email);

        return ResponseEntity.ok().build();
    }

}
