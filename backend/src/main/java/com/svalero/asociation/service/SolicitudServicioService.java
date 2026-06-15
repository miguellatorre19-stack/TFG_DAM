package com.svalero.asociation.service;

import com.svalero.asociation.dto.SolicitudServicioOutDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.exception.ServicioNotFoundException;
import com.svalero.asociation.model.SolicitudServicio;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.repository.SolicitudServicioRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.ServicioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class SolicitudServicioService {

    private final SolicitudServicioRepository solicitudServicioRepository;
    private final ServicioRepository servicioRepository;
    private final ParticipanteRepository participanteRepository;
    private final ModelMapper modelMapper;

    public SolicitudServicioService(SolicitudServicioRepository solicitudServicioRepository,
                                    ServicioRepository servicioRepository, ParticipanteRepository participanteRepository,
                                    ModelMapper modelMapper) {
        this.solicitudServicioRepository = solicitudServicioRepository;
        this.servicioRepository = servicioRepository;
        this.participanteRepository = participanteRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void crearSolicitud(long servicioId, long participanteId, String state, float price) {
        if (solicitudServicioRepository.existsByServicioIdAndParticipanteId(servicioId, participanteId)) {
            throw new BusinessRuleException("Ya existe una solicitud para este participante en este servicio");
        }

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ServicioNotFoundException("Servicio con ID:" + servicioId + " no encontrado"));
        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + participanteId + " no encontrado"));

        if ("ARCHIVED".equalsIgnoreCase(servicio.getStatus())) {
            throw new BusinessRuleException("El servicio con ID " + servicioId + " esta archivado y no admite nuevas solicitudes");
        }

        if (Boolean.FALSE.equals(participante.getActive())) {
            throw new BusinessRuleException("El participante con ID " + participanteId + " esta inactivo y no puede solicitar este servicio");
        }

        SolicitudServicio solicitudServicio = new SolicitudServicio();
        solicitudServicio.setServicio(servicio);
        solicitudServicio.setParticipante(participante);
        solicitudServicio.setState(normalizeState(state, "PENDING"));
        solicitudServicio.setPrice(price);
        solicitudServicioRepository.save(solicitudServicio);
    }

    @Transactional
    public SolicitudServicioOutDto modificarSolicitud(long servicioId, long solicitudId, long participanteId, String state,
            float price) {
        SolicitudServicio solicitud = solicitudServicioRepository.findByIdAndServicioId(solicitudId, servicioId)
                .orElseThrow(() -> new ServicioNotFoundException("Solicitud de servicio no encontrada"));

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + participanteId + " no encontrado"));

        if (Boolean.FALSE.equals(participante.getActive())) {
            throw new BusinessRuleException("El participante con ID " + participanteId + " esta inactivo y no puede usar esta solicitud");
        }

        solicitud.setParticipante(participante);
        solicitud.setState(normalizeState(state, solicitud.getState()));
        solicitud.setPrice(price);

        return toDto(solicitudServicioRepository.save(solicitud));
    }

    @Transactional
    public void cancelarSolicitud(long servicioId, long solicitudId) {
        SolicitudServicio solicitud = solicitudServicioRepository.findByIdAndServicioId(solicitudId, servicioId)
                .orElseThrow(() -> new ServicioNotFoundException("Solicitud de servicio no encontrada"));

        if ("CANCELLED".equalsIgnoreCase(solicitud.getState())) {
            throw new BusinessRuleException("La solicitud de servicio con ID " + solicitudId + " ya fue cancelada");
        }

        solicitud.setState("CANCELLED");
        solicitudServicioRepository.save(solicitud);
    }

    public List<ParticipanteDto> listarParticipantes(long servicioId) {
        return solicitudServicioRepository.findByServicioId(servicioId).stream()
                .map(SolicitudServicio::getParticipante)
                .map(participante -> modelMapper.map(participante, ParticipanteDto.class))
                .toList();
    }

    public List<SolicitudServicioOutDto> listarSolicitudes(long servicioId) {
        return solicitudServicioRepository.findByServicioId(servicioId).stream()
                .map(this::toDto)
                .toList();
    }

    private SolicitudServicioOutDto toDto(SolicitudServicio solicitud) {
        return new SolicitudServicioOutDto(
                solicitud.getId(),
                solicitud.getCreatedAt(),
                solicitud.getState(),
                solicitud.getPrice(),
                solicitud.getParticipante().getId()
        );
    }

    private String normalizeState(String state, String defaultState) {
        if (state == null || state.isBlank()) {
            return defaultState;
        }

        return state.trim().toUpperCase(Locale.ROOT);
    }
}
