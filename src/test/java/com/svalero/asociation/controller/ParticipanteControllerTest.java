package com.svalero.asociation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.service.ParticipanteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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

@WebMvcTest(ParticipanteController.class)
class ParticipanteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParticipanteService participanteService;

    @MockitoBean
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllReturns200() throws Exception {
        ParticipanteOutDto dto1 = buildParticipanteOutDto(1L, "77777777U", "Alberto", 1L);
        ParticipanteOutDto dto2 = buildParticipanteOutDto(2L, "88888888P", "Roberto", 1L);

        when(participanteService.findAll(any(), any(), any())).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/participantes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alberto"))
                .andExpect(jsonPath("$[0].socioID").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Roberto"))
                .andExpect(jsonPath("$[1].socioID").value(1));
    }

    @Test
    void getAllByFiltersReturns200() throws Exception {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        ParticipanteOutDto dto = buildParticipanteOutDto(1L, "77777777U", "Alberto", 1L);

        when(participanteService.findAll(birthDate, "Alberto", "hijo")).thenReturn(List.of(dto));

        mockMvc.perform(get("/participantes")
                        .queryParam("birthDate", birthDate.toString())
                        .queryParam("name", "Alberto")
                        .queryParam("typeRel", "hijo")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alberto"));

        verify(participanteService).findAll(birthDate, "Alberto", "hijo");
    }

    @Test
    void getParticipanteByIdReturns200() throws Exception {
        ParticipanteDto dto = buildParticipanteDto("77777777U", "Alberto", 1L);
        when(participanteService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/participantes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("77777777U"))
                .andExpect(jsonPath("$.name").value("Alberto"))
                .andExpect(jsonPath("$.socioID").value(1));
    }

    @Test
    void getParticipanteByIdReturns404() throws Exception {
        when(participanteService.findById(1L)).thenThrow(new ParticipanteNotFoundException("Participante no encontrado"));

        mockMvc.perform(get("/participantes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addParticipanteDtoReturns201() throws Exception {
        ParticipanteDto requestDto = buildParticipanteDto("77777777U", "Alberto", 33L);
        Participante participanteSaved = buildParticipante(10L, "77777777U", "Alberto", 1L);
        ParticipanteDto mappedResponse = buildParticipanteDto("77777777U", "Alberto", 1L);

        when(participanteService.addDto(any(ParticipanteDto.class), eq(1L))).thenReturn(participanteSaved);
        when(modelMapper.map(participanteSaved, ParticipanteDto.class)).thenReturn(mappedResponse);

        mockMvc.perform(post("/socios/1/participante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("77777777U"))
                .andExpect(jsonPath("$.name").value("Alberto"))
                .andExpect(jsonPath("$.socioID").value(33));
    }

    @Test
    void editParticipanteReturns200() throws Exception {
        ParticipanteDto requestDto = buildParticipanteDto("77777777U", "NuevoNombre", 25L);
        Participante participanteUpdated = buildParticipante(1L, "77777777U", "NuevoNombre", 1L);
        ParticipanteDto mappedResponse = buildParticipanteDto("77777777U", "NuevoNombre", 1L);

        when(participanteService.modifyDto(eq(1L), any(ParticipanteDto.class))).thenReturn(participanteUpdated);
        when(modelMapper.map(participanteUpdated, ParticipanteDto.class)).thenReturn(mappedResponse);

        mockMvc.perform(put("/participantes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NuevoNombre"))
                .andExpect(jsonPath("$.socioID").value(25));
    }

    @Test
    void editParticipanteReturns404() throws Exception {
        ParticipanteDto requestDto = buildParticipanteDto("77777777U", "Alberto", 1L);
        when(participanteService.modifyDto(eq(1L), any(ParticipanteDto.class)))
                .thenThrow(new ParticipanteNotFoundException("Participante no encontrado"));

        mockMvc.perform(put("/participantes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteParticipanteReturns204() throws Exception {
        doNothing().when(participanteService).delete(1L);

        mockMvc.perform(delete("/participantes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteParticipanteReturns404() throws Exception {
        doThrow(new ParticipanteNotFoundException("Participante no encontrado")).when(participanteService).delete(1L);

        mockMvc.perform(delete("/participantes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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
}
