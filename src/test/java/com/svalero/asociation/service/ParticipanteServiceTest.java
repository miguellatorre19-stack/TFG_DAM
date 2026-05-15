package com.svalero.asociation.service;

import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.SocioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
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
import static org.mockito.Mockito.doReturn;
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
    private SocioService socioService;

    @Test
    void testFindAll() {
        LocalDate birthDate = LocalDate.of(2010, 1, 1);
        Participante p1 = buildParticipante(1L, "77777777U", "Alberto", 10L);
        Participante p2 = buildParticipante(2L, "88888888P", "Roberto", 20L);
        List<Participante> participantes = List.of(p1, p2);

        ParticipanteOutDto dto1 = buildParticipanteOutDto(1L, "77777777U", "Alberto", 10L);
        ParticipanteOutDto dto2 = buildParticipanteOutDto(2L, "88888888P", "Roberto", 20L);
        List<ParticipanteOutDto> expected = List.of(dto1, dto2);

        when(participanteRepository.findByFilters(birthDate, "Alberto", "hijo")).thenReturn(participantes);
        doReturn(expected).when(modelMapper).map(eq(participantes), any(Type.class));

        List<ParticipanteOutDto> result = participanteService.findAll(birthDate, "Alberto", "hijo");

        assertEquals(2, result.size());
        assertEquals("Alberto", result.get(0).getName());

        verify(participanteRepository).findByFilters(birthDate, "Alberto", "hijo");
        verify(modelMapper).map(eq(participantes), any(Type.class));
    }

    @Test
    void testFindById() {
        Participante participante = buildParticipante(1L, "77777777U", "Alberto", 1L);
        ParticipanteDto dto = buildParticipanteDto("77777777U", "Alberto", 1L);

        when(participanteRepository.findById(1L)).thenReturn(Optional.of(participante));
        when(modelMapper.map(participante, ParticipanteDto.class)).thenReturn(dto);

        ParticipanteDto result = participanteService.findById(1L);

        assertEquals("Alberto", result.getName());
        assertEquals("77777777U", result.getDni());
        verify(participanteRepository).findById(1L);
        verify(modelMapper).map(participante, ParticipanteDto.class);
    }

    @Test
    void testFindByIdNotFound() {
        when(participanteRepository.findById(99L)).thenReturn(Optional.empty());

        ParticipanteNotFoundException ex = assertThrows(ParticipanteNotFoundException.class,
                () -> participanteService.findById(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(participanteRepository).findById(99L);
        verify(modelMapper, never()).map(any(), eq(ParticipanteDto.class));
    }


    @Test
    void testAddDto() {
        ParticipanteDto participanteDto = buildParticipanteDto("77777777U", "Alberto", 1L);
        Socio socio = new Socio();
        socio.setId(1L);
        SocioDto socioDto = new SocioDto();
        socioDto.setId(1L);

        Participante participante = buildParticipante(1,"77777777U", "Alberto", 1L);

        when(participanteRepository.existsBydni("77777777U")).thenReturn(false);
        when(socioService.findById(1L)).thenReturn(socioDto);
        when(socioRepository.findById(1L)).thenReturn(Optional.of(socio));
        when(participanteRepository.save(any(Participante.class))).thenAnswer(i -> i.getArgument(0));

        Participante result = participanteService.addDto(participanteDto, 1L);

        assertEquals("77777777U", result.getDni());
        assertEquals("Alberto", result.getName());
        assertEquals(1L, result.getSocio().getId());

        verify(modelMapper).map(eq(participanteDto), any(Participante.class));
        verify(participanteRepository).existsBydni("77777777U");
        verify(socioService).findById(1L);
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
    void testDelete() {
        Participante participante = buildParticipante(1L, "77777777U", "Alberto", 1L);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(participante));

        participanteService.delete(1L);

        verify(participanteRepository).findById(1L);
        verify(participanteRepository).delete(participante);
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
