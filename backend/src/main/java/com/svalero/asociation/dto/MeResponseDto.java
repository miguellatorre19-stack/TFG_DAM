package com.svalero.asociation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeResponseDto {
    private Long userId;
    private String name;
    private String email;
    private Set<String> roles;
    private String profileType;
    private Long socioId;
    private Long participanteId;
    private List<Long> participanteIds;
    private Long trabajadorId;
}
