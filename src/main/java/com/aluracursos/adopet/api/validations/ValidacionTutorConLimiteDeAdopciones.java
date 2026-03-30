package com.aluracursos.adopet.api.validations;

import com.aluracursos.adopet.api.dto.SolicitudAdopcionDTO;
import com.aluracursos.adopet.api.exception.ValidacionException;
import com.aluracursos.adopet.api.model.Adopcion;
import com.aluracursos.adopet.api.model.Tutor;
import com.aluracursos.adopet.api.repository.AdopcionRepository;
import com.aluracursos.adopet.api.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidacionTutorConLimiteDeAdopciones implements ValidacionesSolicitudAdopcion {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AdopcionRepository adopcionRepository;


    public void validar(SolicitudAdopcionDTO dto) {
        Tutor tutor = tutorRepository.getReferenceById(dto.idTutor());
        List<Adopcion> adopciones = adopcionRepository.findAll();
        for (Adopcion a : adopciones) {
            int contador = 0;
            if (contador == 5) {
                throw new ValidacionException("Tutor llegó al limite máximo de 5 adopciones!");
            }
        }
    }

}
