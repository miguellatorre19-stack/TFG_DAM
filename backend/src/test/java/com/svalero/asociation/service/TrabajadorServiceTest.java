package com.svalero.asociation.service;

import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.AccessCredentialsDto;
import com.svalero.asociation.dto.TrabajadorAccessResponseDto;
import com.svalero.asociation.dto.TrabajadorDto;
import com.svalero.asociation.dto.TrabajadorOutDto;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.model.Usuario;
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

    @Mock
    private AccessUserService accessUserService;

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
    void testAddDtoWithAccess() {
        TrabajadorDto trabajadorDto = new TrabajadorDto(
                "77777777U",
                "Hector",
                "Aladia",
                "email@email",
                "888-566-323",
                LocalDate.now().minusDays(2),
                null,
                "Tiempo Parcial",
                1L
        );
        Servicio servicio = new Servicio(1, "trabajo social", "anual", "ninguno", 40f, 3, null, null);
        Trabajador trabajador = new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now().minusDays(2), null, "Tiempo Parcial", null, null);
        Usuario usuario = Usuario.builder()
                .id(12L)
                .name("Hector Aladia")
                .email("email@email")
                .password("encoded-password")
                .active(true)
                .build();

        when(mapper.map(trabajadorDto, Trabajador.class)).thenReturn(trabajador);
        when(trabajadorRepository.existsBydni("77777777U")).thenReturn(false);
        when(servicioService.findById(1L)).thenReturn(servicio);
        when(accessUserService.createAccessUser("Hector Aladia", "email@email", "TRABAJADOR"))
                .thenReturn(new AccessCredentialsDto(usuario, "ABCDE-23456"));
        when(trabajadorRepository.save(any(Trabajador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrabajadorAccessResponseDto response = trabajadorService.addDtoWithAccess(trabajadorDto, 1L);

        assertEquals(12L, response.getUsuarioId());
        assertEquals("ABCDE-23456", response.getInitialPassword());
        assertEquals(1L, response.getTrabajador().getId());
        verify(accessUserService).createAccessUser("Hector Aladia", "email@email", "TRABAJADOR");
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
    void testRegenerateAccessCodeExistingUser() {
        Trabajador trabajador = new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null);
        Usuario usuario = Usuario.builder()
                .id(12L)
                .name("Hector Aladia")
                .email("email@email")
                .password("encoded-password")
                .active(true)
                .build();
        trabajador.setUsuario(usuario);

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        when(accessUserService.regenerateAccessCode(usuario))
                .thenReturn(new AccessCredentialsDto(usuario, "ZXCVB-12345"));

        AccessCodeResponseDto response = trabajadorService.regenerateAccessCode(1L);

        assertEquals(12L, response.getUsuarioId());
        assertEquals("ZXCVB-12345", response.getInitialPassword());
        verify(accessUserService).regenerateAccessCode(usuario);
    }

    @Test
    void testDelete() {
        Trabajador trabajador =   new Trabajador(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null, null);
        Usuario usuario = Usuario.builder().id(12L).email("email@email").active(true).build();
        trabajador.setUsuario(usuario);

        when(trabajadorRepository.findById(trabajador.getId())).thenReturn(Optional.of(trabajador));
        when(trabajadorRepository.save(trabajador)).thenReturn(trabajador);

        trabajadorService.delete(trabajador.getId());

        assertFalse(trabajador.getActive());
        assertNotNull(trabajador.getOutDate());
        verify(trabajadorRepository, times(1)).save(trabajador);
        verify(accessUserService).deactivateAccessUser(usuario);
        verify(trabajadorRepository, never()).delete(trabajador);
    }

}
