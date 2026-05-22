package com.svalero.asociation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svalero.asociation.dto.ActividadDto;
import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.exception.ActividadNotFoundException;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.service.ActividadService;
import com.svalero.asociation.service.InscripcionActividadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActividadController.class)
class ActividadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ActividadService actividadService;

    @MockitoBean
    private InscripcionActividadService inscripcionActividadService;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetAllv1Return200() throws Exception {
        ActividadOutDto dto1 = buildActividadOutDto(1L, "Club de lectura");
        ActividadOutDto dto2 = buildActividadOutDto(2L, "Partido de baloncesto");

        when(actividadService.findAll(any(), any(), any())).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/v1/actividades").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Club de lectura"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Partido de baloncesto"));
    }

    @Test
    void testGetAllv1ByFiltersReturn200() throws Exception {
        LocalDate dayActivity = LocalDate.of(2026, 3, 20);
        ActividadOutDto dto = buildActividadOutDto(1L, "Club de lectura");

        when(actividadService.findAll(dayActivity, true, 40f)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/actividades")
                        .queryParam("dayActivity", dayActivity.toString())
                        .queryParam("canJoin", "true")
                        .queryParam("duration", "40")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Club de lectura"));

        verify(actividadService).findAll(dayActivity, true, 40f);
    }

    @Test
    void testGetByIdFor200() throws Exception {
        ActividadOutDto dto = buildActividadOutDto(1L, "Club de lectura");
        when(actividadService.findOutById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/actividades/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Club de lectura"));
    }

    @Test
    void testGetByIdFor404() throws Exception {
        when(actividadService.findOutById(1L)).thenThrow(new ActividadNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/actividades/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddFor201() throws Exception {
        ActividadDto actividadDto = buildActividadDto("Club de lectura");
        ActividadOutDto outDto = buildActividadOutDto(1L, "Club de lectura");

        when(actividadService.add(any(ActividadDto.class))).thenReturn(outDto);

        mockMvc.perform(post("/api/v1/actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actividadDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Club de lectura"));
    }

    @Test
    void testEditFor200() throws Exception {
        Actividad wanted = buildActividadEntity(1L, "Club de lectura", "Individual");
        when(actividadService.modify(eq(1L), any(Actividad.class))).thenReturn(wanted);

        ActividadDto actividadDto = new ActividadDto();
        actividadDto.setDescription("Club de lectura");
        actividadDto.setDayActivity(LocalDate.now().plusDays(7));
        actividadDto.setTypeActivity("Individual");
        actividadDto.setDuration(1.5f);
        actividadDto.setCanJoin(true);
        actividadDto.setCapacity(10);
        actividadDto.setLongitude(-0.8891);
        actividadDto.setLatitude(41.6488);

        mockMvc.perform(put("/api/v1/actividades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actividadDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.typeActivity").value("Individual"));
    }

    @Test
    void testEditFor404() throws Exception {
        ActividadDto actividadDto = new ActividadDto();
        actividadDto.setDescription("Club de lectura");
        actividadDto.setDayActivity(LocalDate.now().plusDays(7));
        actividadDto.setTypeActivity("Individual");
        actividadDto.setDuration(1.5f);
        actividadDto.setCanJoin(true);
        actividadDto.setCapacity(10);
        actividadDto.setLongitude(-0.8891);
        actividadDto.setLatitude(41.6488);

        when(actividadService.modify(eq(1L), any(Actividad.class)))
                .thenThrow(new ActividadNotFoundException("Not found"));

        mockMvc.perform(put("/api/v1/actividades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actividadDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFor204() throws Exception {
        doNothing().when(actividadService).delete(1L);

        mockMvc.perform(delete("/api/v1/actividades/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteFor404() throws Exception {
        doThrow(new ActividadNotFoundException("Not found")).when(actividadService).delete(1L);

        mockMvc.perform(delete("/api/v1/actividades/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

    private Actividad buildActividadEntity(long id, String description, String type) {
        Actividad actividad = new Actividad();
        actividad.setId(id);
        actividad.setDescription(description);
        actividad.setDayActivity(LocalDate.of(2026, 5, 20));
        actividad.setTypeActivity(type);
        actividad.setDuration(40f);
        actividad.setCanJoin(true);
        actividad.setCapacity(10);
        return actividad;
    }
}
