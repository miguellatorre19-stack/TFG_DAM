package com.svalero.asociation.repository;

import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.model.Servicio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServicioRepository extends CrudRepository<Servicio, Long>{
    List<Servicio> findAll();


    @Query("SELECT s FROM servicio s WHERE " +
            "(:periodicity IS NULL OR s.periodicity = :periodicity) AND " +
            "(:capacity IS NULL OR s.capacity = :capacity) AND " +
            "(:duration IS NULL OR s.duration = :duration)")
    List<Servicio> findByFilters(@Param("periodicity") String periodicity,
                                  @Param("capacity") Integer capacity,
                                  @Param("duration") Float duration);
}
