package com.svalero.asociation.service;

import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.repository.TrabajadorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrabajadorServiceTest{

    @InjectMocks
    private TrabajadorService trabajadorService;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private ModelMapper mapper;
    @Mock
    private ServicioService servicioService;

    @Test
    void findAll() {
        List<Trabajador> mockTrabajadorList = List.of(
                new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null),
                new Trabajador(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null, null)
        );
        when(trabajadorRepository.findByFilters(null, null, null)).thenReturn(mockTrabajadorList);

        List<Trabajador> trabajadorList = trabajadorService.findAll(null, null, null);

        assertEquals(2, trabajadorList.size());
        assertEquals("Diana", trabajadorList.getLast().getName());

        verify(trabajadorRepository, times(1)).findByFilters(null, null, null);

    }

    @Test
    void findAllByEntryDateAfter() {
        List<Trabajador> mockTrabajadorList = List.of(
                new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now().plusDays(1), "Tiempo Parcial", null, null),
                new Trabajador(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null, null)
        );
        when(trabajadorRepository.findByFilters(LocalDate.now(), null, null)).thenReturn(mockTrabajadorList);

        List<Trabajador> trabajadorList = trabajadorService.findAll(LocalDate.now(), null, null);

        assertEquals(2, trabajadorList.size());
        assertEquals("Diana", trabajadorList.getLast().getName());


        verify(trabajadorRepository, times(1)).findByFilters(LocalDate.now(), null, null);
    }

    @Test
    void findByNameStartingWithIgnoreCase(){
        List<Trabajador> mockTrabajadorList = List.of(
                new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null),
                new Trabajador(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null, null)
        );
        when(trabajadorRepository.findByFilters(null, "Diana", null)).thenReturn(mockTrabajadorList);

        List<Trabajador> trabajadorList = trabajadorService.findAll(null, "Diana", null);

        assertEquals(2, trabajadorList.size());
        assertEquals("Hector", trabajadorList.getFirst().getName());

        verify(trabajadorRepository, times(1)).findByFilters(null, "Diana", null);

    }

    @Test
    void findByContractType(){
        List<Trabajador> mockTrabajadorList = List.of(
                new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null),
                new Trabajador(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null, null)
        );
        when(trabajadorRepository.findByFilters(null, null, "Tiempo Parcial")).thenReturn(mockTrabajadorList);

        List<Trabajador> trabajadorList = trabajadorService.findAll(null, null, "Tiempo Parcial");

        assertEquals(2, trabajadorList.size());
        assertEquals("Hector", trabajadorList.getFirst().getName());

        verify(trabajadorRepository, times(1)).findByFilters(null, null, "Tiempo Parcial");
    }

    @Test
    void findById() {
        Trabajador selectedTrabajador =   new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null);

        when(trabajadorRepository.findById(selectedTrabajador.getId())).thenReturn(Optional.of(selectedTrabajador));

        Trabajador result = trabajadorService.findById(selectedTrabajador.getId());

        assertEquals("Hector", result.getName());

    }

    @Test
    void testAdd() {
        Trabajador newTrabajador =   new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null);
        Servicio servicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);

        when(servicioService.findById(1)).thenReturn(servicio);
        when(trabajadorRepository.save(newTrabajador)).thenReturn(newTrabajador);
        Trabajador result = trabajadorService.add(newTrabajador, 1);

        assertEquals("Hector", result.getName());
        assertNotNull(result.getServicios());
        assertEquals(1, result.getServicios().getId());
        verify(trabajadorRepository, times(1)).save(newTrabajador);
        verify(servicioService, times(1)).findById(1);
    }

    @Test
    void testModify() {

        Trabajador oldTrabajador =   new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null);
        Trabajador wantedTrabajador =   new Trabajador(1, "1112777K", "Gustavo", "Aladia", "email@email", "982-966-710", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null);

        when(trabajadorRepository.findById(oldTrabajador.getId())).thenReturn(Optional.of(oldTrabajador));

        when(trabajadorRepository.save(oldTrabajador)).thenReturn(wantedTrabajador);

        mapper.map(wantedTrabajador, oldTrabajador);

        Trabajador result = trabajadorService.modify(oldTrabajador.getId(), wantedTrabajador);


        assertEquals("Gustavo", result.getName());
        verify(trabajadorRepository).findById(oldTrabajador.getId());
        verify(trabajadorRepository, times(1)).save(oldTrabajador);
    }

    @Test
    void testModify_ReassignServicio() {
        Servicio oldServicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);
        Servicio newServicio = new Servicio(2, "terapia", "semanal", "ninguno", 10f, 1, null, null);

        Trabajador oldTrabajador = new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now().minusDays(1), LocalDate.now(), "Tiempo Parcial", null, oldServicio);

        Trabajador wantedTrabajador = new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now().minusDays(1), LocalDate.now(), "Tiempo Parcial", null, new Servicio(2, null, null, null, null, null, null, null));

        when(trabajadorRepository.findById(oldTrabajador.getId())).thenReturn(Optional.of(oldTrabajador));
        when(servicioService.findById(2)).thenReturn(newServicio);
        when(trabajadorRepository.save(oldTrabajador)).thenReturn(oldTrabajador);

        Trabajador result = trabajadorService.modify(oldTrabajador.getId(), wantedTrabajador);

        assertNotNull(result.getServicios());
        assertEquals(2, result.getServicios().getId());
        verify(servicioService, times(1)).findById(2);
        verify(trabajadorRepository, times(1)).save(oldTrabajador);
    }

    @Test
    void testModify_RemoveServicio() {
        Servicio oldServicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);
        Trabajador oldTrabajador = new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now().minusDays(1), LocalDate.now(), "Tiempo Parcial", null, oldServicio);

        Trabajador wantedTrabajador = new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now().minusDays(1), LocalDate.now(), "Tiempo Parcial", null, null);

        when(trabajadorRepository.findById(oldTrabajador.getId())).thenReturn(Optional.of(oldTrabajador));
        when(trabajadorRepository.save(oldTrabajador)).thenReturn(oldTrabajador);

        Trabajador result = trabajadorService.modify(oldTrabajador.getId(), wantedTrabajador);

        assertNull(result.getServicios());
        verify(servicioService, never()).findById(anyLong());
        verify(trabajadorRepository, times(1)).save(oldTrabajador);
    }

    @Test
    void testDelete() {
        Trabajador trabajador =   new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null);

        when(trabajadorRepository.findById(trabajador.getId())).thenReturn(Optional.of(trabajador));

        trabajadorService.delete(trabajador.getId());

        verify(trabajadorRepository, times(1)).delete(trabajador);
    }

}
