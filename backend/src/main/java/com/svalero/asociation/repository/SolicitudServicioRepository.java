package com.svalero.asociation.repository;

import com.svalero.asociation.model.SolicitudServicio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudServicioRepository extends CrudRepository<SolicitudServicio, Long> {
    boolean existsByServicioIdAndParticipanteId(long servicioId, long participanteId);
    List<SolicitudServicio> findByServicioId(long servicioId);
    List<SolicitudServicio> findByParticipanteIdIn(List<Long> participanteIds);

    Optional<SolicitudServicio> findByIdAndServicioId(long id, long servicioId);

}
