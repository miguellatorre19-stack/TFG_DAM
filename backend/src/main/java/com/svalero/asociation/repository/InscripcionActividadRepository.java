package com.svalero.asociation.repository;

import com.svalero.asociation.model.InscripcionActividad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionActividadRepository extends CrudRepository<InscripcionActividad, Long> {
    boolean existsByActividadIdAndParticipanteId(long actividadId, long participanteId);
    boolean existsByParticipanteId(long participanteId);
    List<InscripcionActividad> findByActividadId(long actividadId);
    Optional<InscripcionActividad> findByIdAndActividadId(long id, long actividadId);
    void deleteByIdAndActividadId(long id, long actividadId);
    void deleteByParticipanteId(long participanteId);

}
