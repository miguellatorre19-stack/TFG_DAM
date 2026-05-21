package com.svalero.asociation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "inscripcion_actividad",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inscripcion_actividad_participante",
                columnNames = {"actividad_id", "participante_id"}
        )
)
@Entity(name = "inscripcion_actividad")
public class InscripcionActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private float price;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
        if (state == null || state.isBlank()) {
            state = "ACTIVE";
        }
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;
}
