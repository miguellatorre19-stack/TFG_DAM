package com.svalero.asociation.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "actividad")
@Table(name="actividad")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotBlank(message = "necesita una descripción")
    private String description;
    @Column(name = "day_activity")
    @NotNull(message = "necesita una una fecha")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dayActivity;
    @Column (name = "type_activity")
    @NotBlank(message = "necesita una tipo de Actividad")
    private String typeActivity;
    @Column(precision = 2)
    @Positive
    private Float duration;
    @Column(name = "can_join")
    @NotNull
    private Boolean canJoin;
    @Column(precision = 2)
    @Positive
    private Integer capacity;
    @Column
    private Double longitude;
    @Column
    private Double latitude;
    @Column(nullable = true)
    @Embedded
    private Ubicacion place;
    @Column(nullable = false)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "actividad")
    private List<InscripcionActividad> inscripciones;

    @OneToMany(mappedBy = "actividad")
    @JsonBackReference(value = "actividad_trabajadores")
    private List<Trabajador> trabajadoresAsignados;

    @PrePersist
    public void prePersist() {
        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }
}
