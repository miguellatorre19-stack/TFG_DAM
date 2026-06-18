package com.svalero.asociation.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svalero.asociation.dto.BajaRequestDto;
import com.svalero.asociation.dto.TrabajadorAccessResponseDto;
import com.svalero.asociation.dto.TrabajadorDto;
import com.svalero.asociation.dto.TrabajadorOutDto;
import com.svalero.asociation.exception.TrabajadorNotFoundException;
import com.svalero.asociation.service.TrabajadorService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrabajadorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TrabajadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    public TrabajadorService trabajadorService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModelMapper modelmapper;

    @Test
    public void testFindAll_Return200() throws Exception {

        List<TrabajadorOutDto> mockTrabajadorList = List.of(
                new TrabajadorOutDto(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null),
                new TrabajadorOutDto(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null)
        );

        when(trabajadorService.findAllDto(null, null, null)).thenReturn(mockTrabajadorList);

        //simulamos cliente Http                                   llamamos a findAll de Controller
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trabajadores")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();

        List<TrabajadorOutDto> trabajadorList = thisObjectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertNotNull(trabajadorList);
        assertEquals("Hector", trabajadorList.get(0).getName());
        assertEquals(2, trabajadorList.size());
    }

    @Test
    public void testFindAll_ByEntryDate() throws Exception {

        LocalDate filterDate = LocalDate.now();

        List<TrabajadorOutDto> mockTrabajadorList = List.of(
                new TrabajadorOutDto(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null),
                new TrabajadorOutDto(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null)
        );

        when(trabajadorService.findAllDto(filterDate, null,null)).thenReturn(mockTrabajadorList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trabajadores")
                        .queryParam("entryDate", LocalDate.now().toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();
        List<TrabajadorOutDto> trabajadorListResponse = thisObjectMapper.readValue(jsonResponse, new TypeReference<>() {});


        assertEquals("Hector", trabajadorListResponse.get(0).getName());
    }

    @Test
    public void testFindAll_ByName() throws Exception {

        List<TrabajadorOutDto> mockTrabajadorList = List.of(
                new TrabajadorOutDto(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null),
                new TrabajadorOutDto(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null)
        );

        when(trabajadorService.findAllDto(null, "Diana", null)).thenReturn(mockTrabajadorList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trabajadores")
                        .queryParam("name", "Diana")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();

        assertNotNull(jsonResponse);

        List<TrabajadorOutDto> trabajadoresList = thisObjectMapper.readValue(jsonResponse,
                new TypeReference<>() {
                });

        assertEquals("Diana", trabajadoresList.get(1).getName());
    }

    @Test
    public void testFindAll_ContractType() throws Exception {

        List<TrabajadorOutDto> mockTrabajadorList = List.of(
                new TrabajadorOutDto(1, "77777777U", "Hector", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Parcial", null),
                new TrabajadorOutDto(2, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null)
        );

        when(trabajadorService.findAllDto(null, null, "Tiempo Parcial")).thenReturn(mockTrabajadorList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trabajadores")
                        .queryParam("contractType", "Tiempo Parcial")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        String jsonResponse = result.getResponse().getContentAsString();

        assertNotNull(jsonResponse);

        List<TrabajadorOutDto> trabajadoresList = thisObjectMapper.readValue(jsonResponse,
                new TypeReference<>() {
                });

        assertEquals("Hector", trabajadoresList.get(0).getName());
    }

    @Test
    public void testFindAll_Return404() throws Exception {

        when(trabajadorService.findAllDto(any(), any(), any()))
                .thenThrow(new TrabajadorNotFoundException("No trabajadores"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trabajadores"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindById_Return200()throws Exception{
        TrabajadorOutDto selected = new TrabajadorOutDto(1, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null);

        when(trabajadorService.findDtoById(1)).thenReturn(selected);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trabajadores/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        TrabajadorOutDto trabajadorResult = objectMapper.readValue(jsonResponse, TrabajadorOutDto.class);

        assertEquals(1, trabajadorResult.getId());
    }

    @Test
    public void testAddTrabajador_Return201() throws Exception {
        TrabajadorDto newTrabajador = new TrabajadorDto("11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now().minusDays(1), LocalDate.now(), "Tiempo Completo", 1);
        TrabajadorOutDto createdTrabajador = new TrabajadorOutDto(1, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now().minusDays(1), LocalDate.now(), "Tiempo Completo", null);
        TrabajadorAccessResponseDto accessResponse = new TrabajadorAccessResponseDto(
                createdTrabajador,
                30L,
                "email@email",
                "ABCDE-23456"
        );

        ObjectMapper thisObjectmapper = new ObjectMapper();
        thisObjectmapper.registerModule(new JavaTimeModule());

        when(trabajadorService.addDtoWithAccess(any(TrabajadorDto.class), eq(1L))).thenReturn(accessResponse);

        String jsonRequest = thisObjectmapper.writeValueAsString(newTrabajador);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/servicios/1/trabajadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        TrabajadorAccessResponseDto responseTrabajador = thisObjectmapper.readValue(jsonResponse, TrabajadorAccessResponseDto.class);

        assertNotNull(responseTrabajador);
        assertEquals(1, responseTrabajador.getTrabajador().getId());
        assertEquals("Diana", responseTrabajador.getTrabajador().getName());
        assertEquals(30L, responseTrabajador.getUsuarioId());
        assertEquals("ABCDE-23456", responseTrabajador.getInitialPassword());
    }

    @Test
    public void testAddTrabajador_Return400() throws Exception {

        TrabajadorDto newTrabajador = new TrabajadorDto("11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Contrato Temporal", 1);

        newTrabajador.setName(null);

        String jsonRequest = objectMapper.writeValueAsString(newTrabajador);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/servicios/1/trabajadores")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditTrabajador_For200() throws Exception {
        TrabajadorDto wantedTrabajador = new TrabajadorDto("11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.of(1995, 5, 10), LocalDate.now(), "Contrato Temporal", 1);
        TrabajadorOutDto updatedTrabajador = new TrabajadorOutDto(1, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.of(1995, 5, 10), LocalDate.now(), "Contrato Temporal", null);

        ObjectMapper thisObjectmapper = new ObjectMapper();
        thisObjectmapper.registerModule(new JavaTimeModule());

        when(trabajadorService.modifyDto(eq(1L), any(TrabajadorDto.class))).thenReturn(updatedTrabajador);

        String jsonRequest = thisObjectmapper.writeValueAsString(wantedTrabajador);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/trabajadores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());


        TrabajadorDto responseActividad = thisObjectmapper.readValue(jsonRequest, TrabajadorDto.class);
        assertEquals("Diana", responseActividad.getName());
    }

    @Test
    void testEditTrabajador_For404() throws Exception {
        TrabajadorDto wantedTrabajador = new TrabajadorDto("11177777P", "Diana", "Aladia", "email@email.com", "888-566-323", LocalDate.of(1995, 5, 10), LocalDate.now(), "Contrato Temporal", 1);

        ObjectMapper thisObjectmapper = new ObjectMapper();
        thisObjectmapper.registerModule(new JavaTimeModule());

        when(trabajadorService.modifyDto(anyLong(), any(TrabajadorDto.class)))
                .thenThrow(new TrabajadorNotFoundException("Trabajador Not Found"));

        String jsonRequest = thisObjectmapper.writeValueAsString(wantedTrabajador);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/trabajadores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }


    @Test
    void testBajaTrabajador_For204() throws Exception{
        TrabajadorOutDto selected = new TrabajadorOutDto(1, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null);
        BajaRequestDto bajaRequestDto = new BajaRequestDto("Solicitud interna", LocalDate.now());

        doNothing().when(trabajadorService).darDeBaja(eq(selected.getId()), any(BajaRequestDto.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trabajadores/" + selected.getId() + "/baja")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bajaRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testBajaTrabajador_For404() throws Exception{
        TrabajadorOutDto selected = new TrabajadorOutDto(1, "11177777P", "Diana", "Aladia", "email@email", "888-566-323", LocalDate.now(), LocalDate.now(), "Tiempo Completo", null);
        BajaRequestDto bajaRequestDto = new BajaRequestDto("Solicitud interna", LocalDate.now());

        doThrow(new TrabajadorNotFoundException("Trabajador Not Found"))
                .when(trabajadorService).darDeBaja(eq(selected.getId()), any(BajaRequestDto.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trabajadores/" + selected.getId() + "/baja")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bajaRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

