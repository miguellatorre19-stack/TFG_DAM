package com.svalero.asociation.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioDto {
    @NotBlank(message = "necesita una descripción")
    private String description;
    private String periodicity;
    private String requisites;
    @Positive
    @NotNull(message = "necesita una duración")
    private Float duration;
    @Positive
    @NotNull(message = "necesita una capacidad")
    private Integer capacity;
}
