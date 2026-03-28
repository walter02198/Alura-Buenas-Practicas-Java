package com.aluracursos.adopet.api.service;

import com.aluracursos.adopet.api.exception.ValidacionException;
import com.aluracursos.adopet.api.model.Adopcion;
import com.aluracursos.adopet.api.model.StatusAdopcion;
import com.aluracursos.adopet.api.repository.AdopcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdopcionService {

    @Autowired
    private AdopcionRepository repository;

    @Autowired
    private EmailService emailService;

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

        emailService.enviarEmail(
                adopcion.getMascota().getRefugio().getEmail(),
                "Solicitación de adopción",
                "Hola " + adopcion.getMascota().getRefugio().getNombre() + "!\n\nUna solicitud de adopción fue registrada hoy para la mascota: " + adopcion.getMascota().getNombre() + ". \nPor favor, evaluarla para aprobación o reprobación.");

    }

    public void aprobar(Adopcion adopcion) {
        adopcion.setStatus(StatusAdopcion.APROBADO);
        repository.save(adopcion);

        emailService.enviarEmail(
                adopcion.getTutor().getEmail(),
                "Adopción aprobada",
                "Felicitaciones " + adopcion.getTutor().getNombre() + "!\n\nSu adopción de la mascota " + adopcion.getMascota().getNombre() + ", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", fue aprobada.\nPor favor, entrar en contacto con el refugio " + adopcion.getMascota().getRefugio().getNombre() + " para ir a buscar a su mascota.");


    }

    public void reprobar(Adopcion adopcion) {
        adopcion.setStatus(StatusAdopcion.REPROBADO);
        repository.save(adopcion);

        emailService.enviarEmail(
                adopcion.getTutor().getEmail(),
                "Adopción reprobada",
                "Hola " + adopcion.getTutor().getNombre() + "!\n\nInfelizmente su adopción de la mascota " + adopcion.getMascota().getNombre() + ", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", fue reprobada por el refugio " + adopcion.getMascota().getRefugio().getNombre() + " con la seguiente justificativa: " + adopcion.getJustificativaStatus());

    }
}
