package com.svalero.asociation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "participantes")
@Entity(name = "participantes")
public class Participante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    @Pattern(regexp = "\\d{8}[A-Z]")
    @NotBlank(message = "debe tener dni")
    private String dni;
    @Column
    @NotBlank(message = "debe tener nombre")
    private String name;
    @Column
    @NotBlank(message = "debe tener apellido")
    private String surname;
    @Column
    @NotBlank(message = "debe tener email")
    private String email;
    @Column(name = "phone_number")
    @Pattern(regexp="\\d{3}-\\d{3}-\\d{3}")
    @NotBlank(message = "debe tener nº de teléfono")
    private String phoneNumber;
    @Column(name = "birth_date")
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @Column(name = "entry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate = LocalDate.now();
    @Column(columnDefinition = "TEXT")
    private String needs;
    private String typeRel;
    @Column
    @Nullable
    private Boolean active = true;
    private String reason;
    @Column(name ="out_date")
    @Nullable
    private LocalDate outDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id")
    private Socio socio;

    @OneToMany(mappedBy = "participante")
    private List<InscripcionActividad> inscripciones;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    @JsonIgnore
    private Usuario usuario;
}
