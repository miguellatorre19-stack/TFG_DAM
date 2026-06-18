package com.svalero.asociation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BajaRequestDto {
    @NotBlank(message = "debe indicar el motivo de baja")
    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outDate;
}
