package com.svalero.asociation.repository;

import com.svalero.asociation.model.Trabajador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrabajadorRepository extends CrudRepository<Trabajador, Long> {
    List<Trabajador> findAll();
    boolean existsBydni(@Pattern(regexp = "^\\d{8}[A-Z]$") @NotBlank String dni);
    List<Trabajador> findByEntryDateAfter(LocalDate entryDate);
    List<Trabajador> findByNameStartingWithIgnoreCase(String name);
    List<Trabajador> findByContractType(String contractType);

    @Query("SELECT s FROM trabajadores s WHERE " +
            "(:entryDate IS NULL OR s.entryDate >= :entryDate) AND " +
            "(:name IS NULL OR s.name = :name) AND " +
            "(:contractType IS NULL OR s.contractType = :contractType)")
    List<Trabajador> findByFilters(@Param("entryDate") LocalDate entryDate,
                                 @Param("name") String name,
                                 @Param("contractType") String contractType);
}
