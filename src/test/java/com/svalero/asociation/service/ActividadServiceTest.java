package com.svalero.asociation.service;

import com.svalero.asociation.dto.ActividadDto;
import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.exception.ActividadNotFoundException;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.repository.ActividadRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActividadServiceTest {

    @InjectMocks
    private ActividadService actividadService;

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testFindAll() {
        LocalDate day = LocalDate.of(2026, 3, 20);

        Actividad a1 = buildActividad(1L, "Club de lectura");
        Actividad a2 = buildActividad(2L, "Partido de baloncesto");
        List<Actividad> entidades = List.of(a1, a2);

        ActividadOutDto dto1 = buildActividadOutDto(1L, "Club de lectura");
        ActividadOutDto dto2 = buildActividadOutDto(2L, "Partido de baloncesto");
        List<ActividadOutDto> expected = List.of(dto1, dto2);

        when(actividadRepository.findByFilters(day, true, 40f)).thenReturn(entidades);
        doReturn(expected).when(modelMapper).map(eq(entidades), any(Type.class));

        List<ActividadOutDto> result = actividadService.findAll(day, true, 40f);

        assertEquals(2, result.size());
        assertEquals("Club de lectura", result.get(0).getDescription());
        assertEquals("Partido de baloncesto", result.get(1).getDescription());

        verify(actividadRepository).findByFilters(day, true, 40f);
        verify(modelMapper).map(eq(entidades), any(Type.class));
    }

    @Test
    void testFindById() {
        Actividad actividad = buildActividad(1L, "Club de lectura");
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

        Actividad result = actividadService.findById(1L);

        assertSame(actividad, result);
        verify(actividadRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(actividadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ActividadNotFoundException.class, () -> actividadService.findById(99L));

        verify(actividadRepository).findById(99L);
    }

    @Test
    void testFindOutById() {
        Actividad actividad = buildActividad(1L, "Club de lectura");
        ActividadOutDto dto = buildActividadOutDto(1L, "Club de lectura");

        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
        when(modelMapper.map(actividad, ActividadOutDto.class)).thenReturn(dto);

        ActividadOutDto result = actividadService.findOutById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Club de lectura", result.getDescription());
        verify(actividadRepository).findById(1L);
        verify(modelMapper).map(actividad, ActividadOutDto.class);
    }

    @Test
    void testAdd() {
        ActividadDto actividadDto = buildActividadDto("Club de lectura");
        Actividad actividadEntity = buildActividad(1L, "Club de lectura");
        ActividadOutDto outDto = buildActividadOutDto(1L, "Club de lectura");

        when(modelMapper.map(actividadDto, Actividad.class)).thenReturn(actividadEntity);
        when(actividadRepository.save(actividadEntity)).thenReturn(actividadEntity);
        when(modelMapper.map(actividadEntity, ActividadOutDto.class)).thenReturn(outDto);

        ActividadOutDto result = actividadService.add(actividadDto);

        assertEquals(1L, result.getId());
        assertEquals("Club de lectura", result.getDescription());
        verify(modelMapper).map(actividadDto, Actividad.class);
        verify(actividadRepository).save(actividadEntity);
        verify(modelMapper).map(actividadEntity, ActividadOutDto.class);
    }

    @Test
    void testModify() {
        Actividad oldActividad = buildActividad(1L, "Club de lectura");
        Actividad wantedActividad = buildActividad(1L, "Descripcion nueva");
        wantedActividad.setTypeActivity("Individual");
        wantedActividad.setDuration(20f);
        wantedActividad.setCapacity(15);

        when(actividadRepository.findById(1L)).thenReturn(Optional.of(oldActividad));
        doNothing().when(modelMapper).map(eq(wantedActividad), eq(oldActividad));
        when(actividadRepository.save(oldActividad)).thenReturn(wantedActividad);

        Actividad result = actividadService.modify(1L, wantedActividad);

        assertEquals("Descripcion nueva", result.getDescription());
        assertEquals("Individual", result.getTypeActivity());
        assertEquals(20f, result.getDuration());

        verify(actividadRepository).findById(1L);
        verify(modelMapper).map(eq(wantedActividad), eq(oldActividad));
        verify(actividadRepository).save(oldActividad);
    }

    @Test
    void testModifyNotFound() {
        when(actividadRepository.findById(88L)).thenReturn(Optional.empty());

        assertThrows(ActividadNotFoundException.class, () -> actividadService.modify(88L, buildActividad(88L, "x")));

        verify(actividadRepository).findById(88L);
        verify(modelMapper, never()).map(any(Actividad.class), any(Actividad.class));
        verify(actividadRepository, never()).save(any(Actividad.class));
    }

    @Test
    void testDelete() {
        Actividad actividad = buildActividad(1L, "Club de lectura");
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

        actividadService.delete(1L);

        verify(actividadRepository).findById(1L);
        verify(actividadRepository).delete(actividad);
    }

    @Test
    void testDeleteNotFound() {
        when(actividadRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(ActividadNotFoundException.class, () -> actividadService.delete(200L));

        verify(actividadRepository).findById(200L);
        verify(actividadRepository, never()).delete(any(Actividad.class));
    }

    private Actividad buildActividad(long id, String description) {
        Actividad actividad = new Actividad();
        actividad.setId(id);
        actividad.setDescription(description);
        actividad.setDayActivity(LocalDate.of(2026, 5, 20));
        actividad.setTypeActivity("Grupal");
        actividad.setDuration(40f);
        actividad.setCanJoin(true);
        actividad.setCapacity(10);
        return actividad;
    }

    private ActividadDto buildActividadDto(String description) {
        ActividadDto dto = new ActividadDto();
        dto.setDescription(description);
        dto.setDayActivity(LocalDate.of(2026, 5, 20));
        dto.setTypeActivity("Grupal");
        dto.setDuration(40f);
        dto.setCanJoin(true);
        dto.setCapacity(10);
        return dto;
    }

    private ActividadOutDto buildActividadOutDto(long id, String description) {
        ActividadOutDto dto = new ActividadOutDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setDayActivity(LocalDate.of(2026, 5, 20));
        dto.setDuration(40f);
        dto.setCanJoin(true);
        dto.setParticipanteDtoList(List.of());
        return dto;
    }
}
