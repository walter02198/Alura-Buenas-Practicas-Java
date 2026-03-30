package com.aluracursos.adopet.api.validations;

import com.aluracursos.adopet.api.dto.SolicitudAdopcionDTO;
import com.aluracursos.adopet.api.exception.ValidacionException;
import com.aluracursos.adopet.api.model.Adopcion;
import com.aluracursos.adopet.api.model.Mascota;
import com.aluracursos.adopet.api.model.StatusAdopcion;
import com.aluracursos.adopet.api.repository.AdopcionRepository;
import com.aluracursos.adopet.api.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidacionMascotaConAdopcionEnMarcha implements ValidacionesSolicitudAdopcion{

    @Autowired
    private AdopcionRepository adopcionRepository;

    @Autowired
    private MascotaRepository mascotaRepository;


    public void validar(SolicitudAdopcionDTO dto) {
        Mascota mascota = mascotaRepository.getReferenceById(dto.idMascota());
        List<Adopcion> adopciones = adopcionRepository.findAll();
        for (Adopcion a : adopciones) {
            if (a.getMascota() == mascota && a.getStatus() == StatusAdopcion.ESPERANDO_EVALUACION) {
                throw new ValidacionException("Mascota ya esta esperando evaluación para ser adoptada!!");
            }
        }
    }


}
