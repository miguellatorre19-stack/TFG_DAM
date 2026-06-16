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
    private String status;
    private List<Long> TrabajadoresIds;

    public ServicioOutDto(long id, String description, String periodicity, String requisites, Float duration,
            Integer capacity, List<Long> trabajadoresIds) {
        this.id = id;
        this.description = description;
        this.periodicity = periodicity;
        this.requisites = requisites;
        this.duration = duration;
        this.capacity = capacity;
        this.status = "ACTIVE";
        this.TrabajadoresIds = trabajadoresIds;
    }
}
