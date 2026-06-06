package com.svalero.asociation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorAccessResponseDto {
    private TrabajadorOutDto trabajador;
    private Long usuarioId;
    private String email;
    private String initialPassword;
}
