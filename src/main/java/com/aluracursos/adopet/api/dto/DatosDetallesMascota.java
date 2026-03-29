package com.aluracursos.adopet.api.dto;

import com.aluracursos.adopet.api.model.Mascota;
import com.aluracursos.adopet.api.model.TipoMascota;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosDetallesMascota(
        @NotNull
        Long id,
        @NotNull
        TipoMascota tipo,
        @NotBlank
        String nombre,
        @NotBlank
        String raza,
        @NotNull
        Integer edad) {
    public DatosDetallesMascota(Mascota mascota) {
        this(mascota.getId(),
                mascota.getTipo(),
                mascota.getNombre(),
                mascota.getRaza(),
                mascota.getEdad());
    }
}
