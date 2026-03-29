package com.aluracursos.adopet.api.dto;

import com.aluracursos.adopet.api.model.Mascota;
import com.aluracursos.adopet.api.model.TipoMascota;

public record DatosDetallesMascota(Long id,
                                   TipoMascota tipo,
                                   String nombre,
                                   String raza,
                                   Integer edad) {
    public DatosDetallesMascota(Mascota mascota) {
        this(mascota.getId(),
                mascota.getTipo(),
                mascota.getNombre(),
                mascota.getRaza(),
                mascota.getEdad());
    }
}
