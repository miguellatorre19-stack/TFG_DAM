package com.svalero.asociation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrabajadorDto {
    @Pattern(regexp = "^\\d{8}[A-Z]$")
    @NotBlank(message = "necesita un DNI")
    private String dni;
    @NotBlank(message = "necesita un nombre")
    private String name;
    @NotBlank(message = "necesita un apellido")
    private String surname;
    @NotBlank(message = "necesita una email")
    private String email;
    @Pattern(regexp="\\d{3}-\\d{3}-\\d{3}")
    @NotBlank(message = "necesita una tlfno")
    private String phoneNumber;
    @Past
    private LocalDate birthDate;
    private LocalDate entryDate;
    @NotBlank(message = "necesita una tipo de contrato")
    private String contractType;
    private long servicioId;
}
