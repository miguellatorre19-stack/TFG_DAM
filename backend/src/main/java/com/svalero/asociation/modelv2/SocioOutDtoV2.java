package com.svalero.asociation.modelv2;

import com.svalero.asociation.dto.EstadoSocioDto;
import com.svalero.asociation.dto.ParticipanteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocioOutDtoV2 {
    private long id;
    private String dni;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private EstadoSocioDto estadoSocioDto;
    private String familyModel;
    private LocalDate entryDate = LocalDate.now();
    private List<ParticipanteDto> participanteDtoList;
}
