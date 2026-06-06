package com.svalero.asociation.repository;

import com.svalero.asociation.model.Actividad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActividadRepository extends CrudRepository<Actividad, Long> {
    List<Actividad> findAll();

    List<Actividad>findByDayActivity(LocalDate dayActivity);

    List<Actividad> findByDuration(Float duration);

    List<Actividad> findByCanJoin(Boolean canjoin);

    @Query("SELECT s FROM actividad s WHERE " +
            "(:dayActivity IS NULL OR s.dayActivity = :dayActivity) AND " +
            "(:canJoin IS NULL OR s.canJoin = :canJoin) AND " +
            "(:duration IS NULL OR s.duration = :duration)")
    List<Actividad> findByFilters(@Param("dayActivity") LocalDate dayActivity,
                                 @Param("canJoin") Boolean canJoin,
                                 @Param("duration") Float duration);
}
