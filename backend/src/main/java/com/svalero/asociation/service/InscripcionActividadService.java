package com.svalero.asociation.service;

import com.svalero.asociation.dto.InscripcionActividadOutDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.exception.ActividadNotFoundException;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.model.InscripcionActividad;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.repository.ActividadRepository;
import com.svalero.asociation.repository.InscripcionActividadRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InscripcionActividadService {

    @Autowired
    private InscripcionActividadRepository inscripcionActividadRepository;
    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private ParticipanteRepository participanteRepository;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(InscripcionActividadService.class);

    @Transactional
    public void inscribir(long actividadId, long participanteId, String state, float price) {
        if (inscripcionActividadRepository.existsByActividadIdAndParticipanteId(actividadId, participanteId)) {
            throw new BusinessRuleException("El participante ya esta inscrito en esta actividad");
        }

        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ActividadNotFoundException("Actividad con ID:" + actividadId + " no encontrada"));

        if (!Boolean.TRUE.equals(actividad.getCanJoin())) {
            throw new BusinessRuleException("La actividad no admite nuevas inscripciones");
        }

        if (actividad.getCapacity() != null
                && inscripcionActividadRepository.countByActividadId(actividadId) >= actividad.getCapacity()) {
            throw new BusinessRuleException("No quedan plazas disponibles para esta actividad");
        }

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + participanteId + " no encontrado"));

        InscripcionActividad inscripcionActividad = new InscripcionActividad();
        inscripcionActividad.setActividad(actividad);
        inscripcionActividad.setParticipante(participante);
        inscripcionActividad.setState(state);
        inscripcionActividad.setPrice(price);
        inscripcionActividadRepository.save(inscripcionActividad);
        logger.info("Participant ID {} enrolled in actividad ID {}", participanteId, actividadId);
    }

    @Transactional
    public InscripcionActividadOutDto modify(long actividadId, long inscripcionId, long participanteId, String state,
            float price) {
        InscripcionActividad inscripcion = inscripcionActividadRepository.findByIdAndActividadId(inscripcionId, actividadId)
                .orElseThrow(() -> new ActividadNotFoundException("Inscripcion de actividad no encontrada"));

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + participanteId + " no encontrado"));

        inscripcion.setParticipante(participante);
        inscripcion.setState(state);
        inscripcion.setPrice(price);

        return toDto(inscripcionActividadRepository.save(inscripcion));
    }

    @Transactional
    public void deleteInscripcion(long actividadId, long inscripcionId) {
        inscripcionActividadRepository.deleteByIdAndActividadId(inscripcionId, actividadId);
        logger.info("Inscripcion ID {} deleted from actividad ID {}", inscripcionId, actividadId);
    }

    public List<ParticipanteDto> listarParticipantes(long actividadId) {
        return inscripcionActividadRepository.findByActividadId(actividadId).stream()
                .map(InscripcionActividad::getParticipante)
                .map(participante -> modelMapper.map(participante, ParticipanteDto.class))
                .toList();
    }

    public List<InscripcionActividadOutDto> listarInscripciones(long actividadId) {
        return inscripcionActividadRepository.findByActividadId(actividadId).stream()
                .map(this::toDto)
                .toList();
    }

    private InscripcionActividadOutDto toDto(InscripcionActividad inscripcion) {
        return new InscripcionActividadOutDto(
                inscripcion.getId(),
                inscripcion.getCreatedAt(),
                inscripcion.getState(),
                inscripcion.getPrice(),
                inscripcion.getParticipante().getId()
        );
    }
}
