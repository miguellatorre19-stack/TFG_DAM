package com.svalero.asociation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoSocioDto {
    private boolean activo;
    private boolean cuotasAlDia;
    private double importePendiente;
}
