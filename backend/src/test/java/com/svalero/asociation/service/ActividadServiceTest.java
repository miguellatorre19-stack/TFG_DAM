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
import static org.mockito.Mockito.*;

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
        LocalDate dayActivity = LocalDate.of(2026, 5, 20);
        Boolean canJoin = true;
        Float duration = 40.0f;

        Actividad actividad1 = new Actividad();
        actividad1.setId(1L);
        actividad1.setDescription("Club de lectura");
        actividad1.setTypeActivity("Grupal");
        actividad1.setDayActivity(dayActivity);
        actividad1.setDuration(duration);
        actividad1.setCanJoin(canJoin);
        actividad1.setCapacity(10);

        Actividad actividad2 = new Actividad();
        actividad2.setId(2L);
        actividad2.setDescription("Partido de baloncesto");
        actividad2.setTypeActivity("Grupal");
        actividad2.setDayActivity(dayActivity);
        actividad2.setDuration(duration);
        actividad2.setCanJoin(canJoin);
        actividad2.setCapacity(10);

        actividad1.setDayActivity(dayActivity);
        actividad1.setDuration(duration);
        actividad1.setCanJoin(canJoin);
        actividad1.setCapacity(10);

        actividad2.setDayActivity(dayActivity);
        actividad2.setDuration(duration);
        actividad2.setCanJoin(canJoin);
        actividad2.setCapacity(10);

        List<Actividad> actividades = List.of(actividad1, actividad2);

        when(actividadRepository.findByFilters(dayActivity, canJoin, duration))
                .thenReturn(actividades);

        List<ActividadOutDto> result = actividadService.findAll(dayActivity, canJoin, duration);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Club de lectura", result.get(0).getDescription());
        assertEquals(dayActivity, result.get(0).getDayActivity());
        assertEquals(duration, result.get(0).getDuration());
        assertEquals(canJoin, result.get(0).getCanJoin());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Partido de baloncesto", result.get(1).getDescription());

        verify(actividadRepository).findByFilters(dayActivity, canJoin, duration);
        verifyNoInteractions(modelMapper);
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
        when(actividadRepository.save(actividad)).thenReturn(actividad);

        actividadService.delete(1L);

        verify(actividadRepository).findById(1L);
        assertEquals("ARCHIVED", actividad.getStatus());
        verify(actividadRepository).save(actividad);
        verify(actividadRepository, never()).delete(actividad);
    }

    @Test
    void testDeleteNotFound() {
        when(actividadRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(ActividadNotFoundException.class, () -> actividadService.delete(200L));

        verify(actividadRepository).findById(200L);
        verify(actividadRepository, never()).save(any(Actividad.class));
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
        actividad.setStatus("ACTIVE");
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
