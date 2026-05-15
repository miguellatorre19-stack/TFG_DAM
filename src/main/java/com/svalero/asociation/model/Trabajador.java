package com.svalero.asociation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trabajadores")
@Entity(name = "trabajadores")
public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    @Pattern(regexp = "^\\d{8}[A-Z]$")
    @NotBlank(message = "necesita un DNI")
    private String dni;
    @Column
    @NotBlank(message = "necesita un nombre")
    private String name;
    @Column
    @NotBlank(message = "necesita un apellido")
    private String surname;
    @Column
    @NotBlank(message = "necesita una email")
    private String email;
    @Column(name = "phone_number")
    @Pattern(regexp="\\d{3}-\\d{3}-\\d{3}")
    @NotBlank(message = "necesita una tlfno")
    private String phoneNumber;
    @Column(nullable = true, name = "birth_date")
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @Column( name = "entry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "necesita una fecha")
    private LocalDate entryDate;
    @Column
    @NotBlank(message = "necesita una tipo de contrato")
    private String contractType;

    @ManyToOne
    @JoinColumn(name="actividad_id")
    private Actividad actividad;

    @ManyToOne
    @JoinColumn(name="servicio_id")
    private Servicio servicios;
}
