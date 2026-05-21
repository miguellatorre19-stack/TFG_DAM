package com.svalero.asociation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionActividadRequestDto {
    @NotNull(message = "necesita un precio")
    private float price;
    @NotBlank(message = "necesita un estado")
    private String state;
    @NotNull(message = "debe estar vinculado a un participante")
    private long participanteId;
}
