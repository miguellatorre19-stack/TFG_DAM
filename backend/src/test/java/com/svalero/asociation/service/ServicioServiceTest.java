package com.svalero.asociation.service;


import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.repository.ServicioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {
    @InjectMocks
    private ServicioService servicioService;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findAll() {
        List<Servicio> mockServiciosList = List.of(
                new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null),
                new Servicio(2, "terapia", "semanal", "ninguno", 10f, 1, null, null)
        );

        when(servicioRepository.findByFilters(null, null, null)).thenReturn(mockServiciosList);

        List<Servicio> servicioList = servicioService.findAll(null, null, null);

        assertEquals(2, servicioList.size());
        assertEquals("trabajo social", servicioList.getFirst().getDescription());

        verify(servicioRepository, times(1)).findByFilters(null, null, null);

    }

    @Test
    public void testFindByPeriodicity(){
        List<Servicio> mockServicioList = List.of(
                new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null),
                new Servicio(2, "terapia", "semanal", "ninguno", 10f, 1, null, null)
        );

        when(servicioRepository.findByFilters("anual", null, null)).thenReturn(mockServicioList);

        List<Servicio>  servicioList = servicioService.findAll("anual", null, null);

        assertEquals(2, servicioList.size());
        assertEquals("trabajo social", servicioList.getFirst().getDescription());

        verify(servicioRepository, times(1)).findByFilters("anual", null, null);

    }

    @Test
    public void testFindByCapacity(){
        List<Servicio> mockServicioList = List.of(
                new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null),
                new Servicio(2, "terapia", "semanal", "ninguno", 10f, 1, null, null)
        );

        when(servicioRepository.findByFilters(null, 3, null)).thenReturn(mockServicioList);

        List<Servicio>  servicioList = servicioService.findAll(null, 3, null);

        assertEquals(2, servicioList.size());
        assertEquals("trabajo social", servicioList.getFirst().getDescription());

        verify(servicioRepository, times(1)).findByFilters(null, 3, null);

    }

    @Test
    public void testFindByDuration(){
        List<Servicio> mockServicioList = List.of(
                new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null),
                new Servicio(2, "terapia", "semanal", "ninguno", 10f, 1, null, null)
        );

        when(servicioRepository.findByFilters(null, null, 40f)).thenReturn(mockServicioList);

        List<Servicio>  servicioList = servicioService.findAll(null, null, 40f);

        assertEquals(2, servicioList.size());
        assertEquals("trabajo social", servicioList.getFirst().getDescription());

        verify(servicioRepository, times(1)).findByFilters(null, null, 40f);
    }
    
    @Test
    void findById() {
        Servicio selectedServicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);

        when(servicioRepository.findById(selectedServicio.getId())).thenReturn(Optional.of(selectedServicio));

        Servicio result = servicioService.findById(selectedServicio.getId());

        assertEquals("trabajo social", result.getDescription());
    }

    @Test
    void add() {
        Servicio newservicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);

        when(servicioRepository.save(newservicio)).thenReturn(newservicio);
        Servicio result = servicioService.add(newservicio);

        assertEquals("trabajo social", result.getDescription());
        verify(servicioRepository, times(1)).save(newservicio);
    }

    @Test
    void modify() {
        
        Servicio oldservicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);
        Servicio wantedServicio = new Servicio(1, "charla al púlico", "bianual", "ninguno", 40f, 3, null, null);

        when(servicioRepository.findById(oldservicio.getId())).thenReturn(Optional.of(oldservicio));

        when(servicioRepository.save(oldservicio)).thenReturn(wantedServicio);

        Servicio result = servicioService.modify(oldservicio.getId(), wantedServicio);

        mapper.map(wantedServicio, oldservicio);

        assertEquals(40f, result.getDuration());
        verify(servicioRepository).findById(oldservicio.getId());
        verify(servicioRepository, times(1)).save(oldservicio);
    }

    @Test
    void delete() {
        
        Servicio servicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);
        when(servicioRepository.findById(servicio.getId())).thenReturn(Optional.of(servicio));

        servicioService.delete(servicio.getId());

        verify(servicioRepository, times(1)).delete(servicio);

    }
}