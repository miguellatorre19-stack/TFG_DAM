package com.svalero.asociation.repository;

import com.svalero.asociation.model.Participante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface ParticipanteRepository extends CrudRepository<Participante, Long> {
    List <Participante> findAll();

    boolean existsBydni(@Pattern(regexp = "\\d{8}[A-Z]") @NotBlank String dni);

    @Query("SELECT s FROM participantes s JOIN FETCH s.socio WHERE " +
            "(:birthDate IS NULL OR s.birthDate >= :birthDate) AND " +
            "(:name IS NULL OR s.name = :name) AND " +
            "(:typeRel IS NULL OR s.typeRel = :typeRel)")
    List<Participante> findByFilters(@Param("birthDate") LocalDate birthDate,
                                @Param("name") String name,
                                @Param("typeRel") String typeRel);
}
