package com.svalero.asociation.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svalero.asociation.dto.ServicioDto;
import com.svalero.asociation.dto.ServicioOutDto;
import com.svalero.asociation.exception.ServicioNotFoundException;
import static org.mockito.ArgumentMatchers.*;

import com.svalero.asociation.service.ServicioService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServicioController.class)
class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    public ServicioService servicioService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModelMapper modelmapper;

    @Test
    void testGetAllServicio_Return200() throws Exception {

        List<ServicioOutDto> serviciosList = List.of(
                new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of()),
                new ServicioOutDto(2, "terapia", "semanal", "ninguno", 1f, 1, List.of())

        );

        when(servicioService.findAllDto(isNull(), isNull(), isNull())).thenReturn(serviciosList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/servicios")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();

        List<ServicioOutDto> finalServicioList = thisObjectMapper.readValue(jsonResponse, new TypeReference<List<ServicioOutDto>>() {});

        assertNotNull(finalServicioList);
        assertEquals("terapia", finalServicioList.get(1).getDescription());

    }

    @Test
    void testGetAllServicio_ByPeridiocity() throws Exception {

        List<ServicioOutDto> serviciosList = List.of(
                new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of()),
                new ServicioOutDto(2, "terapia", "semanal", "ninguno", 1f, 1, List.of())
        );

        when(servicioService.findAllDto(eq("anual"), isNull(), isNull())).thenReturn(serviciosList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/servicios")
                        .queryParam("periodicity", "anual")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();

        String jsonResponse = result.getResponse().getContentAsString();

        List<ServicioOutDto> servicioListresponse = thisObjectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(servicioListresponse);
        assertEquals("terapia", servicioListresponse.getLast().getDescription());
    }

    @Test
    void testGetAllServicio_ByCapacity() throws Exception {

        List<ServicioOutDto> serviciosList = List.of(
                new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of()),
                new ServicioOutDto(2, "terapia", "semanal", "ninguno", 1f, 1, List.of())
        );

        when(servicioService.findAllDto(isNull(), any(), isNull())).thenReturn(serviciosList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/servicios")
                        .queryParam("capacity", "1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();

        String jsonResponse = result.getResponse().getContentAsString();

        List<ServicioOutDto> servicioListresponse = thisObjectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(servicioListresponse);
        assertEquals("terapia", servicioListresponse.getLast().getDescription());
    }

    @Test
    void testGetAllServicio_ByDuration() throws Exception {

        List<ServicioOutDto> serviciosList = List.of(
                new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of()),
                new ServicioOutDto(2, "terapia", "semanal", "ninguno", 1f, 1, List.of())
        );

        when(servicioService.findAllDto(isNull(), isNull(), any())).thenReturn(serviciosList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/servicios")
                        .queryParam("duration", "40")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();

        String jsonResponse = result.getResponse().getContentAsString();

        List<ServicioOutDto> servicioListresponse = thisObjectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(servicioListresponse);
        assertEquals(40, servicioListresponse.getFirst().getDuration());
    }

    @Test
    void testGetServicioById_For200() throws Exception {
        ServicioOutDto selected = new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of());

        when(servicioService.findDtoById(selected.getId())).thenReturn(selected);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/servicios/"+ selected.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ServicioOutDto servicio = objectMapper.readValue(jsonResponse, ServicioOutDto.class);

        assertEquals(1, servicio.getId());
    }

    @Test
    void testGetServicioById_For404() throws Exception {

        when(servicioService.findDtoById(7)).thenThrow(ServicioNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/servicios/7")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    void testAddServicio_Return200() throws Exception {

        ServicioDto newServicio = new ServicioDto("trabajo social", "anual", "ninguno", 40f, 3);
        ServicioOutDto createdServicio = new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of());

        ObjectMapper thisobjectmapper = new ObjectMapper();
        when(servicioService.addDto(any(ServicioDto.class))).thenReturn(createdServicio);

        String jsonRequest = thisobjectmapper.writeValueAsString(newServicio);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        ServicioOutDto responseServicio = thisobjectmapper.readValue(jsonResponse, ServicioOutDto.class);

        assertNotNull(responseServicio);
        assertEquals("trabajo social", responseServicio.getDescription());
    }

    @Test
    public void testAddServicio_Return400() throws Exception {

        ServicioDto newServicio = new ServicioDto("trabajo social", "anual", "ninguno", 40f, 3);
        newServicio.setDescription(null);

        String jsonRequest = objectMapper.writeValueAsString(newServicio);

        mockMvc.perform(MockMvcRequestBuilders.post("/servicios")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditServicio_For200() throws Exception {

        ServicioOutDto originalServicio = new ServicioOutDto(1, "trabajo social", "diario", "ninguno", 40f, 3, List.of());
        ServicioDto wantedServicio = new ServicioDto("trabajo social", "anual", "ninguno", 40f, 3);
        ServicioOutDto updatedServicio = new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of());

        ObjectMapper thisobjectmapper = new ObjectMapper();
        thisobjectmapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        when(servicioService.modifyDto(eq(1L), any(ServicioDto.class))).thenReturn(updatedServicio);

        String jsonRequest = thisobjectmapper.writeValueAsString(wantedServicio);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/servicios/" + originalServicio.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        ServicioOutDto responseServicio = thisobjectmapper.readValue(jsonResponse, ServicioOutDto.class);
        assertEquals(1, responseServicio.getId());
    }

    @Test
    void testEditServicio_For404() throws Exception {

        ServicioOutDto originalServicio = new ServicioOutDto(1, "trabajo social", "diario", "ninguno", 40f, 3, List.of());
        ServicioDto wantedServicio = new ServicioDto("trabajo social", "anual", "ninguno", 40f, 3);

        ObjectMapper thisobjectmapper = new ObjectMapper();
        thisobjectmapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        when(servicioService.modifyDto(eq(1L), any(ServicioDto.class))).thenThrow(new ServicioNotFoundException("Servicio Not Found"));

        String jsonRequest = thisobjectmapper.writeValueAsString(wantedServicio);

      mockMvc.perform(MockMvcRequestBuilders.put("/servicios/" + originalServicio.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }


    @Test
    void testDeleteServicio_204() throws Exception{
        ServicioOutDto selected = new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of());

        doNothing().when(servicioService).delete(selected.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/servicios/" + selected.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteServicio_404() throws Exception{
        ServicioOutDto selected = new ServicioOutDto(1, "trabajo social", "anual", "ninguno", 40f, 3, List.of());

        doNothing().when(servicioService).delete(selected.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/servicios/" + selected.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
