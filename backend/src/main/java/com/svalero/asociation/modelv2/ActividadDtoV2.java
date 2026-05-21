package com.svalero.asociation.modelv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svalero.asociation.dto.UbicacionDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActividadDtoV2 {
    @NotBlank(message = "necesita una descripción")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dayActivity;
    private String typeActivity;
    @NotNull(message = "necesita una duración")
    private Float duration;
    @NotNull(message = "debe inidcarse si se puede unir o no")
    private Boolean canJoin;
    @NotNull(message = "necesita una capacidad")
    private Integer capacity;
    @NotNull
    private UbicacionDto ubicacion;
}
