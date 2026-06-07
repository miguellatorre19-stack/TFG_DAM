package com.svalero.asociation.service;

import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.AccessCredentialsDto;
import com.svalero.asociation.dto.ParticipanteAccessResponseDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.InscripcionActividadRepository;
import com.svalero.asociation.repository.InscripcionServicioRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.SocioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipanteServiceTest {

    @InjectMocks
    private ParticipanteService participanteService;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private SocioRepository socioRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AccessUserService accessUserService;

    @Mock
    private InscripcionActividadRepository inscripcionActividadRepository;

    @Mock
    private InscripcionServicioRepository inscripcionServicioRepository;

    @Test
    void testFindAll() {
        LocalDate birthDate = LocalDate.of(2010, 1, 1);
        Participante p1 = buildParticipante(1L, "77777777U", "Alberto", 10L);
        Participante p2 = buildParticipante(2L, "88888888P", "Roberto", 20L);
        List<Participante> participantes = List.of(p1, p2);

        when(participanteRepository.findByFilters(birthDate, "Alberto", "hijo")).thenReturn(participantes);

        List<ParticipanteOutDto> result = participanteService.findAll(birthDate, "Alberto", "hijo");

        assertEquals(2, result.size());
        assertEquals("Alberto", result.get(0).getName());
        assertEquals(10L, result.get(0).getSocioID());

        verify(participanteRepository).findByFilters(birthDate, "Alberto", "hijo");
    }

    @Test
    void testFindById() {
        Participante participante = buildParticipante(1L, "77777777U", "Alberto", 1L);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(participante));

        ParticipanteDto result = participanteService.findById(1L);

        assertEquals("Alberto", result.getName());
        assertEquals("77777777U", result.getDni());
        assertEquals(1L, result.getSocioID());
        verify(participanteRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(participanteRepository.findById(99L)).thenReturn(Optional.empty());

        ParticipanteNotFoundException ex = assertThrows(ParticipanteNotFoundException.class,
                () -> participanteService.findById(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(participanteRepository).findById(99L);
    }


    @Test
    void testAddDto() {
        ParticipanteDto participanteDto = buildParticipanteDto("77777777U", "Alberto", 1L);

        Socio socio = new Socio();
        socio.setId(1L);

        doAnswer(invocation -> {
            ParticipanteDto source = invocation.getArgument(0);
            Participante target = invocation.getArgument(1);

            target.setDni(source.getDni());
            target.setName(source.getName());
            target.setSurname(source.getSurname());
            target.setEmail(source.getEmail());
            target.setPhoneNumber(source.getPhoneNumber());
            target.setBirthDate(source.getBirthDate());
            target.setNeeds(source.getNeeds());
            target.setTypeRel(source.getTypeRel());

            return null;
        }).when(modelMapper).map(eq(participanteDto), any(Participante.class));

        when(participanteRepository.existsBydni("77777777U")).thenReturn(false);
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(participanteRepository.save(any(Participante.class))).thenAnswer(i -> i.getArgument(0));

        Participante result = participanteService.addDto(participanteDto, 1L);

        assertEquals("77777777U", result.getDni());
        assertEquals("Alberto", result.getName());
        assertEquals(1L, result.getSocio().getId());

        verify(modelMapper).map(eq(participanteDto), any(Participante.class));
        verify(participanteRepository).existsBydni("77777777U");
        verify(socioRepository).findById(1L);
        verify(participanteRepository).save(any(Participante.class));
    }

    @Test
    void testModify() {
        long id = 1L;
        ParticipanteDto participanteDto = buildParticipanteDto("77777777U", "NuevoNombre", 1L);
        Participante oldParticipante = buildParticipante(id, "77777777U", "Alberto", 1L);

        when(participanteRepository.findById(id)).thenReturn(Optional.of(oldParticipante));
        doAnswer(invocation -> {
            ParticipanteDto source = invocation.getArgument(0);
            Participante target = invocation.getArgument(1);
            target.setName(source.getName());
            target.setDni(source.getDni());
            return null;
        }).when(modelMapper).map(eq(participanteDto), eq(oldParticipante));
        when(participanteRepository.save(oldParticipante)).thenReturn(oldParticipante);

        Participante result = participanteService.modifyDto(id, participanteDto);

        assertSame(oldParticipante, result);
        assertEquals("NuevoNombre", result.getName());

        verify(participanteRepository).findById(id);
        verify(modelMapper).map(eq(participanteDto), eq(oldParticipante));
        verify(participanteRepository).save(oldParticipante);
    }

    @Test
    void testModifyNotFound() {
        ParticipanteDto participanteDto = buildParticipanteDto("77777777U", "Alberto", 1L);
        when(participanteRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(ParticipanteNotFoundException.class, () -> participanteService.modifyDto(77L, participanteDto));

        verify(participanteRepository).findById(77L);
        verify(modelMapper, never()).map(any(), any(Participante.class));
        verify(participanteRepository, never()).save(any(Participante.class));
    }

    @Test
    void testAddDtoWithAccess() {
        ParticipanteDto participanteDto = buildParticipanteDto("77777777U", "Alberto", 1L);
        Socio socio = new Socio();
        socio.setId(1L);
        Usuario usuario = Usuario.builder()
                .id(8L)
                .name("Alberto Gomara")
                .email("email@email.com")
                .password("encoded-password")
                .active(true)
                .build();

        doAnswer(invocation -> {
            ParticipanteDto source = invocation.getArgument(0);
            Participante target = invocation.getArgument(1);
            target.setDni(source.getDni());
            target.setName(source.getName());
            target.setSurname(source.getSurname());
            target.setEmail(source.getEmail());
            target.setPhoneNumber(source.getPhoneNumber());
            target.setBirthDate(source.getBirthDate());
            target.setNeeds(source.getNeeds());
            target.setTypeRel(source.getTypeRel());
            return null;
        }).when(modelMapper).map(eq(participanteDto), any(Participante.class));

        when(participanteRepository.existsBydni("77777777U")).thenReturn(false);
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(accessUserService.createAccessUser("Alberto Gomara", "email@email.com", "PARTICIPANTE"))
                .thenReturn(new AccessCredentialsDto(usuario, "ABCDE-23456"));
        when(participanteRepository.save(any(Participante.class))).thenAnswer(i -> i.getArgument(0));
        ParticipanteAccessResponseDto response = participanteService.addDtoWithAccess(participanteDto, 1L);

        assertEquals(8L, response.getUsuarioId());
        assertEquals("ABCDE-23456", response.getInitialPassword());
        assertEquals("email@email.com", response.getEmail());
        assertEquals(1L, response.getParticipante().getSocioID());
        verify(accessUserService).createAccessUser("Alberto Gomara", "email@email.com", "PARTICIPANTE");
    }

    @Test
    void testRegenerateAccessCodeExistingUser() {
        Participante participante = buildParticipante(1L, "77777777U", "Alberto", 1L);
        Usuario usuario = Usuario.builder()
                .id(8L)
                .name("Alberto Gomara")
                .email("email@email.com")
                .password("encoded-password")
                .active(true)
                .build();
        participante.setUsuario(usuario);

        when(participanteRepository.findById(1L)).thenReturn(Optional.of(participante));
        when(accessUserService.regenerateAccessCode(usuario))
                .thenReturn(new AccessCredentialsDto(usuario, "ZXCVB-12345"));

        AccessCodeResponseDto response = participanteService.regenerateAccessCode(1L);

        assertEquals(8L, response.getUsuarioId());
        assertEquals("ZXCVB-12345", response.getInitialPassword());
        verify(accessUserService).regenerateAccessCode(usuario);
    }

    @Test
    void testDelete() {
        Participante participante = buildParticipante(1L, "77777777U", "Alberto", 1L);
        Usuario usuario = Usuario.builder().id(8L).email("email@email.com").build();
        participante.setUsuario(usuario);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(participante));
        when(inscripcionActividadRepository.existsByParticipanteId(1L)).thenReturn(false);
        when(inscripcionServicioRepository.existsByParticipanteId(1L)).thenReturn(false);

        participanteService.delete(1L);

        verify(participanteRepository).findById(1L);
        verify(participanteRepository).delete(participante);
        verify(accessUserService).deleteAccessUser(usuario);
    }

    @Test
    void testDeleteNotFound() {
        when(participanteRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ParticipanteNotFoundException.class, () -> participanteService.delete(100L));

        verify(participanteRepository).findById(100L);
        verify(participanteRepository, never()).delete(any(Participante.class));
    }

    private Participante buildParticipante(long id, String dni, String name, long socioId) {
        Participante participante = new Participante();
        participante.setId(id);
        participante.setDni(dni);
        participante.setName(name);
        participante.setSurname("Gomara");
        participante.setEmail("email@email.com");
        participante.setPhoneNumber("888-566-323");
        participante.setBirthDate(LocalDate.of(2000, 1, 1));
        participante.setEntryDate(LocalDate.of(2025, 1, 1));
        participante.setNeeds("ninguna");
        participante.setTypeRel("hijo");
        Socio socio = new Socio();
        socio.setId(socioId);
        participante.setSocio(socio);
        return participante;
    }

    private ParticipanteDto buildParticipanteDto(String dni, String name, long socioId) {
        ParticipanteDto dto = new ParticipanteDto();
        dto.setDni(dni);
        dto.setName(name);
        dto.setSurname("Gomara");
        dto.setEmail("email@email.com");
        dto.setPhoneNumber("888-566-323");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        dto.setNeeds("ninguna");
        dto.setTypeRel("hijo");
        dto.setSocioID(socioId);
        return dto;
    }

    private ParticipanteOutDto buildParticipanteOutDto(long id, String dni, String name, long socioId) {
        ParticipanteOutDto dto = new ParticipanteOutDto();
        dto.setId(id);
        dto.setDni(dni);
        dto.setName(name);
        dto.setSurname("Gomara");
        dto.setEmail("email@email.com");
        dto.setPhoneNumber("888-566-323");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        dto.setNeeds("ninguna");
        dto.setTypeRel("hijo");
        dto.setSocioID(socioId);
        return dto;
    }
}
