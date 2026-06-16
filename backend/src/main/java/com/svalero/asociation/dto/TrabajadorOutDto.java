package com.svalero.asociation.dto;

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
    private String reason;
    private ServicioOutDto servicioOutDto;

    public TrabajadorOutDto(long id, String dni, String name, String surname, String email, String phoneNumber,
            LocalDate birthDate, LocalDate entryDate, String contractType, ServicioOutDto servicioOutDto) {
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.entryDate = entryDate;
        this.contractType = contractType;
        this.active = true;
        this.outDate = null;
        this.servicioOutDto = servicioOutDto;
    }
}
