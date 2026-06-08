package com.svalero.asociation.service;

import com.svalero.asociation.dto.InscripcionServicioOutDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.exception.ServicioNotFoundException;
import com.svalero.asociation.model.InscripcionServicio;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.repository.InscripcionServicioRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.ServicioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InscripcionServicioService {

    private final InscripcionServicioRepository inscripcionServicioRepository;
    private final ServicioRepository servicioRepository;
    private final ParticipanteRepository participanteRepository;
    private final ModelMapper modelMapper;

    public InscripcionServicioService(InscripcionServicioRepository inscripcionServicioRepository,
            ServicioRepository servicioRepository, ParticipanteRepository participanteRepository,
            ModelMapper modelMapper) {
        this.inscripcionServicioRepository = inscripcionServicioRepository;
        this.servicioRepository = servicioRepository;
        this.participanteRepository = participanteRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void inscribir(long servicioId, long participanteId, String state, float price) {
        if (inscripcionServicioRepository.existsByServicioIdAndParticipanteId(servicioId, participanteId)) {
            throw new BusinessRuleException("El participante ya esta inscrito en este servicio");
        }

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ServicioNotFoundException("Servicio con ID:" + servicioId + " no encontrado"));

        if (servicio.getCapacity() != null
                && inscripcionServicioRepository.countByServicioId(servicioId) >= servicio.getCapacity()) {
            throw new BusinessRuleException("No quedan plazas disponibles para este servicio");
        }

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + participanteId + " no encontrado"));

        InscripcionServicio inscripcionServicio = new InscripcionServicio();
        inscripcionServicio.setServicio(servicio);
        inscripcionServicio.setParticipante(participante);
        inscripcionServicio.setState(state);
        inscripcionServicio.setPrice(price);
        inscripcionServicioRepository.save(inscripcionServicio);
    }

    @Transactional
    public InscripcionServicioOutDto modify(long servicioId, long inscripcionId, long participanteId, String state,
            float price) {
        InscripcionServicio inscripcion = inscripcionServicioRepository.findByIdAndServicioId(inscripcionId, servicioId)
                .orElseThrow(() -> new ServicioNotFoundException("Inscripcion de servicio no encontrada"));

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + participanteId + " no encontrado"));

        inscripcion.setParticipante(participante);
        inscripcion.setState(state);
        inscripcion.setPrice(price);

        return toDto(inscripcionServicioRepository.save(inscripcion));
    }

    @Transactional
    public void deleteInscripcion(long servicioId, long inscripcionId) {
        inscripcionServicioRepository.deleteByIdAndServicioId(inscripcionId, servicioId);
    }

    public List<ParticipanteDto> listarParticipantes(long servicioId) {
        return inscripcionServicioRepository.findByServicioId(servicioId).stream()
                .map(InscripcionServicio::getParticipante)
                .map(participante -> modelMapper.map(participante, ParticipanteDto.class))
                .toList();
    }

    public List<InscripcionServicioOutDto> listarInscripciones(long servicioId) {
        return inscripcionServicioRepository.findByServicioId(servicioId).stream()
                .map(this::toDto)
                .toList();
    }

    private InscripcionServicioOutDto toDto(InscripcionServicio inscripcion) {
        return new InscripcionServicioOutDto(
                inscripcion.getId(),
                inscripcion.getCreatedAt(),
                inscripcion.getState(),
                inscripcion.getPrice(),
                inscripcion.getParticipante().getId()
        );
    }
}
