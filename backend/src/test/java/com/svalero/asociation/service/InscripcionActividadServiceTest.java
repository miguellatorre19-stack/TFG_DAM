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
    void inscribirThrowsWhenParticipanteAlreadyEnrolled() {
        Actividad actividad = new Actividad();
        actividad.setId(1L);

        when(inscripcionActividadRepository.existsByActividadIdAndParticipanteId(1L, 2L)).thenReturn(true);

        assertThrows(BusinessRuleException.class,
                () -> inscripcionActividadService.inscribir(1L, 2L, "ENVIADA", 0));

        verify(actividadRepository, never()).findById(1L);
        verify(participanteRepository, never()).findById(2L);
        verify(inscripcionActividadRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void inscribirThrowsWhenParticipanteDoesNotExist() {
        Actividad actividad = new Actividad();
        actividad.setId(1L);

        when(inscripcionActividadRepository.existsByActividadIdAndParticipanteId(1L, 2L)).thenReturn(false);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
        when(participanteRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(com.svalero.asociation.exception.ParticipanteNotFoundException.class,
                () -> inscripcionActividadService.inscribir(1L, 2L, "ENVIADA", 0));

        verify(participanteRepository).findById(2L);
        verify(inscripcionActividadRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
