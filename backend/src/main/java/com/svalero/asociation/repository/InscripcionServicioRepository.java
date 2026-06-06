package com.svalero.asociation.repository;

import com.svalero.asociation.model.InscripcionServicio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionServicioRepository extends CrudRepository<InscripcionServicio, Long> {
    boolean existsByServicioIdAndParticipanteId(long servicioId, long participanteId);

    List<InscripcionServicio> findByServicioId(long servicioId);

    Optional<InscripcionServicio> findByIdAndServicioId(long id, long servicioId);

    void deleteByIdAndServicioId(long id, long servicioId);
}
