package com.svalero.asociation.service;

import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.repository.ActividadRepository;
import com.svalero.asociation.repository.InscripcionActividadRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionActividadServiceTest {

    @InjectMocks
    private InscripcionActividadService inscripcionActividadService;

    @Mock
    private InscripcionActividadRepository inscripcionActividadRepository;

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void inscribirThrowsWhenActividadDoesNotAllowRegistrations() {
        Actividad actividad = new Actividad();
        actividad.setId(1L);
        actividad.setCanJoin(false);
        actividad.setCapacity(10);

        when(inscripcionActividadRepository.existsByActividadIdAndParticipanteId(1L, 2L)).thenReturn(false);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

        assertThrows(BusinessRuleException.class,
                () -> inscripcionActividadService.inscribir(1L, 2L, "ENVIADA", 0));

        verify(participanteRepository, never()).findById(2L);
        verify(inscripcionActividadRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void inscribirThrowsWhenActividadIsFull() {
        Actividad actividad = new Actividad();
        actividad.setId(1L);
        actividad.setCanJoin(true);
        actividad.setCapacity(1);

        when(inscripcionActividadRepository.existsByActividadIdAndParticipanteId(1L, 2L)).thenReturn(false);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
        when(inscripcionActividadRepository.countByActividadId(1L)).thenReturn(1L);

        assertThrows(BusinessRuleException.class,
                () -> inscripcionActividadService.inscribir(1L, 2L, "ENVIADA", 0));

        verify(participanteRepository, never()).findById(2L);
        verify(inscripcionActividadRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
