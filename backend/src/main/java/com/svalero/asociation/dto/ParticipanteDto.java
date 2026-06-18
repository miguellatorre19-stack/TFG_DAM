package com.svalero.asociation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteDto {
    @Pattern(regexp = "\\d{8}[A-Z]")
    @NotBlank(message = "debe tener dni")
    private String dni;
    @NotBlank(message = "debe tener nombre")
    private String name;
    @NotBlank(message = "debe tener apellidos")
    private String surname;
    @NotBlank(message = "debe tener email")
    private String email;
    @Pattern(regexp="\\d{3}-\\d{3}-\\d{3}")
    private String phoneNumber;
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String needs;
    private String typeRel;
    private long socioID;
}

