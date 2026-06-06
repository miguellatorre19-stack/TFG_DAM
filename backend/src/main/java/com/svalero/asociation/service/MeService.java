package com.svalero.asociation.service;

import com.svalero.asociation.dto.MeResponseDto;
import com.svalero.asociation.exception.ResourceNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.SocioRepository;
import com.svalero.asociation.repository.TrabajadorRepository;
import com.svalero.asociation.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeService {

    private final UsuarioRepository usuarioRepository;
    private final SocioRepository socioRepository;
    private final ParticipanteRepository participanteRepository;
    private final TrabajadorRepository trabajadorRepository;

    public MeService(UsuarioRepository usuarioRepository, SocioRepository socioRepository,
            ParticipanteRepository participanteRepository, TrabajadorRepository trabajadorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.socioRepository = socioRepository;
        this.participanteRepository = participanteRepository;
        this.trabajadorRepository = trabajadorRepository;
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
}
