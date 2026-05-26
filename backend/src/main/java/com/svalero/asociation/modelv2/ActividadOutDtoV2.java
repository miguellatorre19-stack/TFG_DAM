package com.svalero.asociation.modelv2;

import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.UbicacionOutDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActividadOutDtoV2 {
    private long id;
    private String description;
    private LocalDate dayActivity;
    private Float duration;
    private Boolean canJoin;
    private List<ParticipanteDto> participanteDtoList;
    private UbicacionOutDto ubicacionOutDto;
}
