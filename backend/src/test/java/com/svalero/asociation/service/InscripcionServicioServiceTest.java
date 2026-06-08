package com.svalero.asociation.service;

import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.repository.InscripcionServicioRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.ServicioRepository;
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
class InscripcionServicioServiceTest {

    @InjectMocks
    private InscripcionServicioService inscripcionServicioService;

    @Mock
    private InscripcionServicioRepository inscripcionServicioRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void inscribirThrowsWhenServicioIsFull() {
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setCapacity(1);

        when(inscripcionServicioRepository.existsByServicioIdAndParticipanteId(1L, 2L)).thenReturn(false);
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(inscripcionServicioRepository.countByServicioId(1L)).thenReturn(1L);

        assertThrows(BusinessRuleException.class,
                () -> inscripcionServicioService.inscribir(1L, 2L, "ENVIADA", 0));

        verify(participanteRepository, never()).findById(2L);
        verify(inscripcionServicioRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
