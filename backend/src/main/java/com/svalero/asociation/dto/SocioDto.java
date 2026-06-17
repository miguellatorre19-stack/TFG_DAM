package com.svalero.asociation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocioDto {
    private long id;
    @Pattern(regexp = "\\d{8}[A-Z]")
    @NotBlank
    private String dni;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String email;
    @Pattern(regexp="\\d{3}-\\d{3}-\\d{3}")
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private Boolean active;
    @NotBlank
    private String familyModel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;

    private List<ParticipanteDto> participanteDtoList;

    public SocioDto(long id, String dni, String name, String surname, String email, String phoneNumber,
            Boolean active, String familyModel, LocalDate entryDate, List<ParticipanteDto> participanteDtoList) {
        this(id, dni, name, surname, email, phoneNumber, active, familyModel, entryDate, null, null,
                participanteDtoList);
    }
}
