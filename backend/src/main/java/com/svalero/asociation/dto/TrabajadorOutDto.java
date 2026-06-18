package com.svalero.asociation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrabajadorOutDto {
    private long id;
    private String dni;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private LocalDate entryDate;
    private String contractType;
    private Boolean active;
    private LocalDate outDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;
    private ActividadOutDto actividadOutDto;
    private ServicioOutDto servicioOutDto;

    public TrabajadorOutDto(long id, String dni, String name, String surname, String email, String phoneNumber,
            LocalDate birthDate, LocalDate entryDate, String contractType, ServicioOutDto servicioOutDto) {
        this(id, dni, name, surname, email, phoneNumber, birthDate, entryDate, contractType, true, null, null,
                null, servicioOutDto);
    }
}
