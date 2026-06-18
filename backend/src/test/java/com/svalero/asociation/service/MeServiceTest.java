package com.svalero.asociation.service;

import com.svalero.asociation.dto.PrivateAreaRequestDto;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.model.InscripcionActividad;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.model.SolicitudServicio;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.InscripcionActividadRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.SocioRepository;
import com.svalero.asociation.repository.SolicitudServicioRepository;
import com.svalero.asociation.repository.TrabajadorRepository;
import com.svalero.asociation.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeServiceTest {

    @InjectMocks
    private MeService meService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SocioRepository socioRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private InscripcionActividadRepository inscripcionActividadRepository;

    @Mock
    private SolicitudServicioRepository solicitudServicioRepository;

    @Test
    void testGetCurrentRequestsReturnsCombinedRequestsOrderedByDate() {
        Usuario usuario = buildUsuario("familia@example.com", "SOCIO");
        Participante participante1 = buildParticipante(11L, "Lucia");
        Participante participante2 = buildParticipante(12L, "Mario");
        Socio socio = new Socio();
        socio.setId(3L);
        socio.setParticipanteList(List.of(participante1, participante2));

        InscripcionActividad inscripcion = buildInscripcionActividad(
                21L,
                LocalDate.of(2026, 6, 17),
                "ENVIADA",
                participante1,
                8L,
                "Teatro"
        );
        SolicitudServicio solicitud = buildSolicitudServicio(
                22L,
                LocalDate.of(2026, 6, 18),
                "PENDING",
                participante2,
                5L,
                "Logopedia"
        );

        when(usuarioRepository.findByEmail("familia@example.com")).thenReturn(Optional.of(usuario));
        when(socioRepository.findByUsuarioEmail("familia@example.com")).thenReturn(Optional.of(socio));
        when(participanteRepository.findByUsuarioEmail("familia@example.com")).thenReturn(Optional.empty());
        when(trabajadorRepository.findByUsuarioEmail("familia@example.com")).thenReturn(Optional.empty());
        when(inscripcionActividadRepository.findByParticipanteIdIn(List.of(11L, 12L)))
                .thenReturn(List.of(inscripcion));
        when(solicitudServicioRepository.findByParticipanteIdIn(List.of(11L, 12L)))
                .thenReturn(List.of(solicitud));

        List<PrivateAreaRequestDto> result = meService.getCurrentRequests("familia@example.com");

        assertEquals(2, result.size());
        assertEquals("servicio", result.get(0).getKind());
        assertEquals("Logopedia", result.get(0).getTitle());
        assertEquals(12L, result.get(0).getParticipanteId());
        assertEquals("actividad", result.get(1).getKind());
        assertEquals("Teatro", result.get(1).getTitle());

        verify(inscripcionActividadRepository).findByParticipanteIdIn(List.of(11L, 12L));
        verify(solicitudServicioRepository).findByParticipanteIdIn(List.of(11L, 12L));
    }

    @Test
    void testGetCurrentRequestsReturnsEmptyWhenUserHasNoParticipants() {
        Usuario usuario = buildUsuario("trabajo@example.com", "TRABAJADOR");

        when(usuarioRepository.findByEmail("trabajo@example.com")).thenReturn(Optional.of(usuario));
        when(socioRepository.findByUsuarioEmail("trabajo@example.com")).thenReturn(Optional.empty());
        when(participanteRepository.findByUsuarioEmail("trabajo@example.com")).thenReturn(Optional.empty());
        when(trabajadorRepository.findByUsuarioEmail("trabajo@example.com")).thenReturn(Optional.empty());

        List<PrivateAreaRequestDto> result = meService.getCurrentRequests("trabajo@example.com");

        assertTrue(result.isEmpty());
        verify(inscripcionActividadRepository, never()).findByParticipanteIdIn(anyList());
        verify(solicitudServicioRepository, never()).findByParticipanteIdIn(anyList());
    }

    private Usuario buildUsuario(String email, String roleName) {
        return Usuario.builder()
                .id(1L)
                .name("Usuario")
                .email(email)
                .password("secret")
                .active(true)
                .roles(Set.of(Rol.builder().id(1L).name(roleName).build()))
                .build();
    }

    private Participante buildParticipante(long id, String name) {
        Participante participante = new Participante();
        participante.setId(id);
        participante.setName(name);
        participante.setSurname("Prueba");
        participante.setDni("12345678A");
        participante.setEmail(name.toLowerCase() + "@example.com");
        participante.setActive(true);
        return participante;
    }

    private InscripcionActividad buildInscripcionActividad(
            long id,
            LocalDate createdAt,
            String state,
            Participante participante,
            long actividadId,
            String actividadTitle
    ) {
        Actividad actividad = new Actividad();
        actividad.setId(actividadId);
        actividad.setDescription(actividadTitle);

        InscripcionActividad inscripcion = new InscripcionActividad();
        inscripcion.setId(id);
        inscripcion.setCreatedAt(createdAt);
        inscripcion.setState(state);
        inscripcion.setPrice(0f);
        inscripcion.setParticipante(participante);
        inscripcion.setActividad(actividad);
        return inscripcion;
    }

    private SolicitudServicio buildSolicitudServicio(
            long id,
            LocalDate createdAt,
            String state,
            Participante participante,
            long servicioId,
            String servicioTitle
    ) {
        Servicio servicio = new Servicio();
        servicio.setId(servicioId);
        servicio.setDescription(servicioTitle);

        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setId(id);
        solicitud.setCreatedAt(createdAt);
        solicitud.setState(state);
        solicitud.setPrice(0f);
        solicitud.setParticipante(participante);
        solicitud.setServicio(servicio);
        return solicitud;
    }
}
