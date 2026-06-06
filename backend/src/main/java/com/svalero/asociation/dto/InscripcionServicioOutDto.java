package com.svalero.asociation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionServicioOutDto {
    private long id;
    private LocalDate createdAt;
    private String state;
    private float price;
    private long participanteId;
}
