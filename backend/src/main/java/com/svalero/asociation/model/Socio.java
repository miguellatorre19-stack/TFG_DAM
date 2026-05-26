package com.svalero.asociation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.svalero.asociation.dto.ParticipanteDto;
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
@Table(name = "socio")
@Entity(name = "socio")
public class Socio {
        @Id
        @GeneratedValue (strategy = GenerationType.IDENTITY)
        private long id; // bugfix a futuro, en postman se puede cambiar toda un registro con POST si editas la id
        @Column(unique = true)
        @Pattern(regexp = "\\d{8}[A-Z]")
        @NotBlank(message = "necesita un DNI")
        private String dni;
        @Column
        @NotBlank(message = "necesita un nombre")
        private String name;
        @Column
        @NotBlank(message = "necesita un apellido")
        private String surname;
        @Column(nullable = false)
        @NotBlank(message = "necesita un email")
        private String email;
        @Column
        private String address;
        @Column(name = "phone_number")
        @Pattern(regexp="\\d{3}-\\d{3}-\\d{3}")
        @NotBlank(message = "necesita un tlfno")
        private String phoneNumber;
        @Column(nullable = true, name = "family_model")
        private String familyModel;
        @Column(name = "active")
        private Boolean active;
        @Column(name = "entry_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate entryDate = LocalDate.now();
        @Column(nullable = true)
        @Null
        private LocalDate outDate;

        @OneToMany(mappedBy = "socio")
        @JsonBackReference
        private List<Participante> participanteList;
}
