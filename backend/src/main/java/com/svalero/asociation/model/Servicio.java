package com.svalero.asociation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "servicio")
@Table(name = "servicio")
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotBlank(message = "necesita una descripción")
    private String description;
    @Column
    private String periodicity;
    @Column
    private String requisites;
    @Column(precision = 2)
    @Positive
    @NotNull(message = "necesita una duración")
    private Float duration;
    @Column(precision = 2)
    @Positive
    @NotNull(message = "necesita una capacidad")
    private Integer capacity;

    @ManyToMany(mappedBy = "servicios")
    private List<Participante> participantesInscritos;

    @OneToMany(mappedBy = "servicios")
    @JsonBackReference(value = "servicio_trabajadores")
    private List<Trabajador> trabajadoresAsignados;


}
