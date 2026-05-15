package com.svalero.asociation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActividadOutDto {
    private long id;
    private String description;
    private LocalDate dayActivity;
    private Float duration;
    private Boolean canJoin;
    private List<ParticipanteDto> participanteDtoList;
}
