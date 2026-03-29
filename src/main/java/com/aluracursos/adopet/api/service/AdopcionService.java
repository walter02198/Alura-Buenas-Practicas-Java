package com.aluracursos.adopet.api.service;

import com.aluracursos.adopet.api.dto.AprobacionAdopcionDTO;
import com.aluracursos.adopet.api.dto.ReprobacionAdopcionDTO;
import com.aluracursos.adopet.api.dto.SoilcitudAdopcionDTO;
import com.aluracursos.adopet.api.exception.ValidacionException;
import com.aluracursos.adopet.api.model.Adopcion;
import com.aluracursos.adopet.api.model.Mascota;
import com.aluracursos.adopet.api.model.StatusAdopcion;
import com.aluracursos.adopet.api.model.Tutor;
import com.aluracursos.adopet.api.repository.AdopcionRepository;
import com.aluracursos.adopet.api.repository.MascotaRepository;
import com.aluracursos.adopet.api.repository.TutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdopcionService {

    @Autowired
    private AdopcionRepository adopcionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private TutorRepository tutorRepository;


    public void solicitar( SoilcitudAdopcionDTO dto) {
        Mascota mascota = mascotaRepository.getReferenceById(dto.idMascota());
        Tutor tutor = tutorRepository.getReferenceById(dto.idTutor());

        if (mascota.getAdoptada()) {
            throw new ValidacionException("Mascota ya fue adoptada!");
        } else {
            List<Adopcion> adopciones = adopcionRepository.findAll();
            for (Adopcion a : adopciones) {
                if (a.getTutor() == tutor && a.getStatus() == StatusAdopcion.ESPERANDO_EVALUACION) {
                    throw new ValidacionException("Tutor ya tiene otra adopción esperando evaluación!");

                }
            }
            for (Adopcion a : adopciones) {
                if (a.getMascota() == mascota && a.getStatus() == StatusAdopcion.ESPERANDO_EVALUACION) {
                    throw new ValidacionException("Mascota ya esta esperando evaluación para ser adoptada!!");

                }
            }
            for (Adopcion a : adopciones) {
                int contador = 0;
                if (a.getTutor() == tutor && a.getStatus() == StatusAdopcion.APROBADO) {
                    contador = contador + 1;
                }
                if (contador == 5) {
                    throw new ValidacionException("Tutor llegó al limite máximo de 5 adopciones!");

                }
            }
        }

        Adopcion adopcion = new Adopcion();
        adopcion.setFecha(LocalDateTime.now());
        adopcion.setStatus(StatusAdopcion.ESPERANDO_EVALUACION);

        adopcion.setTutor(tutor);
        adopcion.setMascota(mascota);
        adopcion.setMotivo(dto.motivo());
        adopcionRepository.save(adopcion);

        emailService.enviarEmail(
                adopcion.getMascota().getRefugio().getEmail(),
                "Solicitación de adopción",
                "Hola " + adopcion.getMascota().getRefugio().getNombre() + "!\n\nUna solicitud de adopción fue registrada hoy para la mascota: " + adopcion.getMascota().getNombre() + ". \nPor favor, evaluarla para aprobación o reprobación.");

    }

    public void aprobar(@Valid AprobacionAdopcionDTO dto) {
        Adopcion adopcion = adopcionRepository.getReferenceById(dto.idAdopcion());
        adopcion.setStatus(StatusAdopcion.APROBADO);

        emailService.enviarEmail(
                adopcion.getTutor().getEmail(),
                "Adopción aprobada",
                "Felicitaciones " + adopcion.getTutor().getNombre() + "!\n\nSu adopción de la mascota " + adopcion.getMascota().getNombre() + ", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", fue aprobada.\nPor favor, entrar en contacto con el refugio " + adopcion.getMascota().getRefugio().getNombre() + " para ir a buscar a su mascota.");


    }

    public void reprobar(@Valid ReprobacionAdopcionDTO dto) {

        Adopcion adopcion = adopcionRepository.getReferenceById(dto.idAdopcion());
        adopcion.setStatus(StatusAdopcion.REPROBADO);
        adopcion.setJustificacionStatus(dto.justificacion());

        emailService.enviarEmail(
                adopcion.getTutor().getEmail(),
                "Adopción reprobada",
                "Hola " + adopcion.getTutor().getNombre() + "!\n\nInfelizmente su adopción de la mascota " + adopcion.getMascota().getNombre() + ", solicitada el dia " + adopcion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", fue reprobada por el refugio " + adopcion.getMascota().getRefugio().getNombre() + " con la seguiente justificativa: " + adopcion.getJustificativaStatus());

    }
}
