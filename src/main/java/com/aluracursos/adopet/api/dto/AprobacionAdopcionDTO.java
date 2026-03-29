package com.aluracursos.adopet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AprobacionAdopcionDTO(
        @NotNull
        Long idAdopcion) {
}
