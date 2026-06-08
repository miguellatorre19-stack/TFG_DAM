package com.svalero.asociation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteAccessResponseDto {
    private ParticipanteOutDto participante;
    private Long usuarioId;
    private String email;
    private String initialPassword;
}
