package com.svalero.asociation.service;

import com.svalero.asociation.dto.MeResponseDto;
import com.svalero.asociation.dto.PrivateAreaRequestDto;
import com.svalero.asociation.exception.ResourceNotFoundException;
import com.svalero.asociation.model.InscripcionActividad;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.SolicitudServicio;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.InscripcionActividadRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.SocioRepository;
import com.svalero.asociation.repository.SolicitudServicioRepository;
import com.svalero.asociation.repository.TrabajadorRepository;
import com.svalero.asociation.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MeService {

    private final UsuarioRepository usuarioRepository;
    private final SocioRepository socioRepository;
    private final ParticipanteRepository participanteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final InscripcionActividadRepository inscripcionActividadRepository;
    private final SolicitudServicioRepository solicitudServicioRepository;

    public MeService(UsuarioRepository usuarioRepository, SocioRepository socioRepository,
            ParticipanteRepository participanteRepository, TrabajadorRepository trabajadorRepository,
            InscripcionActividadRepository inscripcionActividadRepository,
            SolicitudServicioRepository solicitudServicioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.socioRepository = socioRepository;
        this.participanteRepository = participanteRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.inscripcionActividadRepository = inscripcionActividadRepository;
        this.solicitudServicioRepository = solicitudServicioRepository;
    }

    @Transactional(readOnly = true)
    public MeResponseDto getCurrentUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getName)
                .collect(Collectors.toSet());

        MeResponseDto response = new MeResponseDto(
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                roles,
                "USUARIO",
                null,
                null,
                List.of(),
                null
        );

        socioRepository.findByUsuarioEmail(email).ifPresent(socio -> fillSocio(response, socio));
        participanteRepository.findByUsuarioEmail(email).ifPresent(participante -> fillParticipante(response, participante));
        trabajadorRepository.findByUsuarioEmail(email).ifPresent(trabajador -> fillTrabajador(response, trabajador));

        return response;
    }

    @Transactional(readOnly = true)
    public List<PrivateAreaRequestDto> getCurrentRequests(String email) {
        List<Long> participanteIds = getCurrentUser(email).getParticipanteIds();

        if (participanteIds == null || participanteIds.isEmpty()) {
            return List.of();
        }

        Stream<PrivateAreaRequestDto> activityRequests = inscripcionActividadRepository
                .findByParticipanteIdIn(participanteIds)
                .stream()
                .map(this::toActivityRequestDto);

        Stream<PrivateAreaRequestDto> serviceRequests = solicitudServicioRepository
                .findByParticipanteIdIn(participanteIds)
                .stream()
                .map(this::toServiceRequestDto);

        return Stream.concat(activityRequests, serviceRequests)
                .sorted(Comparator
                        .comparing(PrivateAreaRequestDto::getCreatedAt, Comparator.reverseOrder())
                        .thenComparing(PrivateAreaRequestDto::getId, Comparator.reverseOrder()))
                .toList();
    }

    private void fillSocio(MeResponseDto response, Socio socio) {
        response.setProfileType("SOCIO");
        response.setSocioId(socio.getId());
        response.setParticipanteIds(
                socio.getParticipanteList() == null
                        ? List.of()
                        : socio.getParticipanteList().stream()
                                .map(Participante::getId)
                                .toList()
        );
    }

    private void fillParticipante(MeResponseDto response, Participante participante) {
        response.setProfileType("PARTICIPANTE");
        response.setParticipanteId(participante.getId());
        response.setParticipanteIds(List.of(participante.getId()));
        if (participante.getSocio() != null) {
            response.setSocioId(participante.getSocio().getId());
        }
    }

    private void fillTrabajador(MeResponseDto response, Trabajador trabajador) {
        response.setProfileType("TRABAJADOR");
        response.setTrabajadorId(trabajador.getId());
    }

    private PrivateAreaRequestDto toActivityRequestDto(InscripcionActividad inscripcion) {
        String title = inscripcion.getActividad().getDescription();
        if (title == null || title.isBlank()) {
            title = "Actividad #" + inscripcion.getActividad().getId();
        }

        return new PrivateAreaRequestDto(
                inscripcion.getId(),
                "actividad",
                inscripcion.getActividad().getId(),
                title,
                inscripcion.getCreatedAt(),
                inscripcion.getState(),
                inscripcion.getPrice(),
                inscripcion.getParticipante().getId()
        );
    }

    private PrivateAreaRequestDto toServiceRequestDto(SolicitudServicio solicitud) {
        String title = solicitud.getServicio().getDescription();
        if (title == null || title.isBlank()) {
            title = "Servicio #" + solicitud.getServicio().getId();
        }

        return new PrivateAreaRequestDto(
                solicitud.getId(),
                "servicio",
                solicitud.getServicio().getId(),
                title,
                solicitud.getCreatedAt(),
                solicitud.getState(),
                solicitud.getPrice(),
                solicitud.getParticipante().getId()
        );
    }
}
