package com.aluracursos.adopet.api.service;

import com.aluracursos.adopet.api.exception.ValidacionException;
import com.aluracursos.adopet.api.model.Adopcion;
import com.aluracursos.adopet.api.model.StatusAdopcion;
import com.aluracursos.adopet.api.repository.AdopcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;

public class AdopcionService {

    @Autowired
    private AdopcionRepository repository;

    @Autowired
    private JavaMailSender emailSender;

    public void solicitar(Adopcion adopcion) {

        if (adopcion.getMascota().getAdoptada() == true) {
            throw new ValidacionException("Mascota ya fue adoptada!");

        } else {
            List<Adopcion> adopciones = repository.findAll();
            for (Adopcion a : adopciones) {
                if (a.getTutor() == adopcion.getTutor() && a.getStatus() == StatusAdopcion.ESPERANDO_EVALUACION) {
                    throw new ValidacionException("Tutor ya tiene otra adopción esperando evaluación!");

                }
            }
            for (Adopcion a : adopciones) {
                if (a.getMascota() == adopcion.getMascota() && a.getStatus() == StatusAdopcion.ESPERANDO_EVALUACION) {
                    throw new ValidacionException("Mascota ya esta esperando evaluación para ser adoptada!!");

                }
            }
            for (Adopcion a : adopciones) {
                int contador = 0;
                if (a.getTutor() == adopcion.getTutor() && a.getStatus() == StatusAdopcion.APROBADO) {
                    contador = contador + 1;
                }
                if (contador == 5) {
                    throw new ValidacionException("Tutor llegó al limite máximo de 5 adopciones!");

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

    }

    public void aprobar() {

    }

    public void desaprobar() {

    }
}
