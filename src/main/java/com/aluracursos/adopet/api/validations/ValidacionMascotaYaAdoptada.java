package com.aluracursos.adopet.api.validations;


import com.aluracursos.adopet.api.dto.SoilcitudAdopcionDTO;
import com.aluracursos.adopet.api.exception.ValidacionException;
import com.aluracursos.adopet.api.model.Mascota;
import com.aluracursos.adopet.api.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidacionMascotaYaAdoptada {

    @Autowired
    private MascotaRepository mascotaRepository;

    public void validar(SoilcitudAdopcionDTO dto) {
        Mascota mascota = mascotaRepository.getReferenceById(dto.idMascota());
        if (mascota.getAdoptada()) {
            throw new ValidacionException("Mascota ya fue adoptada!");
        }
    }
}
