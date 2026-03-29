package com.aluracursos.adopet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SoilcitudAdopcionDTO(
        @NotNull
        Long idTutor,
        @NotNull
        Long idMascota,
        @NotBlank
        String motivo) {
}
