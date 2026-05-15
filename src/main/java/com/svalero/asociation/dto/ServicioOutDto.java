package com.svalero.asociation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioOutDto {
    private long id;
    private String description;
    private String periodicity;
    private String requisites;
    private Float duration;
    private Integer capacity;
    private List<Long> TrabajadoresIds;
}
