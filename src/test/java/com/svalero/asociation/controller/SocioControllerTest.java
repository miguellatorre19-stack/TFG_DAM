package com.svalero.asociation.controller;
import com.fasterxml.jackson.core.type.TypeReference;
import static org.mockito.ArgumentMatchers.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.SocioNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.service.ParticipanteService;
import com.svalero.asociation.service.SocioService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocioController.class)
public class SocioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    public SocioService socioService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModelMapper modelmapper;

    @MockitoBean
    private ParticipanteService participanteService;

    @Test
    public void testFindAllSocio_Return200() throws Exception {

        List<Socio> mockSocioList = List.of(
                new Socio(1, "77777777U", "Alberto", "Gomara", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.of(2000, 5,6), null, null),
                new Socio(2, "77777327U", "Juan", "Izabal", "email@email.com", "calle2", "888-566-323", "Monoparental", true, LocalDate.of(2000, 5,6), null, null)
        );

        ModelMapper thismodelMapper = new ModelMapper();
        List<SocioDto> mockSocioDtoList = thismodelMapper.map(mockSocioList, new TypeToken<List<SocioDto>>() {}.getType());

        when(socioService.findAll(isNull(), isNull(), isNull())).thenReturn(mockSocioDtoList);

        //simulamos cliente Http                                   llamamos a findAll de Controller
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/socios")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();

        List<SocioDto> socioDtoListResponse = thisObjectMapper.readValue(jsonResponse, new TypeReference<List<SocioDto>>() {});

        assertNotNull(mockSocioDtoList);
        assertEquals("Alberto", socioDtoListResponse.get(0).getName());
        assertEquals(2, socioDtoListResponse.size());
    }

    @Test
    public void testFindAllSocio_ByEntryDate() throws Exception {

        LocalDate filterDate = LocalDate.of(2000, 5, 6);

        List<Socio> mockSocioList = List.of(
                new Socio(1, "77777777U", "Alberto", "Gomara", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.of(2000, 5,6), null, null),
                new Socio(2, "77777327U", "Juan", "Izabal", "email@email.com", "calle2", "888-566-323", "Monoparental", true, LocalDate.of(2000, 5,6), null, null)
        );

        ModelMapper thismodelMapper = new ModelMapper();
        List<SocioDto> mockSocioDtoList = thismodelMapper.map(mockSocioList, new TypeToken<List<SocioDto>>() {}.getType());

        when(socioService.findAll( isNull(), isNull(),eq(filterDate))).thenReturn(mockSocioDtoList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/socios")
                        .queryParam("entryDate", "2000-05-06")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();
        List<SocioDto> socioDtoListResponse = thisObjectMapper.readValue(jsonResponse, new TypeReference<List<SocioDto>>() {});

        assertFalse(socioDtoListResponse.isEmpty(), "La lista sigue vacía, revisa el mapeo del Controlador");
        assertEquals("Alberto", socioDtoListResponse.get(0).getName());
    }

    @Test
    public void testFindAllSocio_ByFamilyModel() throws Exception {

        List<Socio> mockSocioList = List.of(
                new Socio(1, "77777777U", "Alberto", "Gomara", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.of(2000, 5,6), null, null),
                new Socio(2, "77777327U", "Juan", "Izabal", "email@email.com", "calle2", "888-566-323", "Monoparental", true, LocalDate.of(2000, 5,6), null, null)
        );

        ModelMapper thismodelMapper = new ModelMapper();
        List<SocioDto> mockSocioDtoList = thismodelMapper.map(mockSocioList, new TypeToken<List<SocioDto>>() {}.getType());


        when(socioService.findAll("Monoparental",null, null)).thenReturn(mockSocioDtoList);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/socios")
                        .queryParam("familyModel", "Monoparental")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();


        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();


        assertNotNull(jsonResponse);
        assertFalse(jsonResponse.isEmpty(), "La respuesta JSON llegó vacía");

        List<SocioDto> socioDtoListResponse = thisObjectMapper.readValue(jsonResponse,
                new TypeReference<List<SocioDto>>() {});

        assertEquals("Juan", socioDtoListResponse.get(1).getName());
    }

    @Test
    public void testFindAllSocio_ByActive() throws Exception {

        List<Socio> mockSocioList = List.of(
                new Socio(1, "77777777U", "Alberto", "Gomara", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.of(2000, 5,6), null, null),
                new Socio(2, "77777327U", "Juan", "Izabal", "email@email.com", "calle2", "888-566-323", "Monoparental", true, LocalDate.of(2000, 5,6), null, null)

        );

        ModelMapper thismodelMapper = new ModelMapper();

        List<SocioDto> mockSocioDtoList = thismodelMapper.map(mockSocioList, new TypeToken<List<SocioDto>>() {}.getType());

        when(socioService.findAll(any(), any(), any())).thenReturn(mockSocioDtoList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/socios")
                        .queryParam("active", "true")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper thisObjectMapper = new ObjectMapper();
        thisObjectMapper.registerModule(new JavaTimeModule());
        thisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonResponse = result.getResponse().getContentAsString();

        List<SocioDto> socioDtoListResponse = thisObjectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertNotNull(socioDtoListResponse);
        assertEquals("Alberto", socioDtoListResponse.get(0).getName());
    }

    @Test
    public void testFindSocio_ById_Return200()throws Exception{

        Socio selected = new Socio(2, "77777777U", "Alberto", "Gomara", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, null);

        ModelMapper thismodelmapper = new ModelMapper();

        SocioDto selectedDto = thismodelmapper.map(selected, SocioDto.class);

        when(socioService.findById(selectedDto.getId())).thenReturn(selectedDto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/socios/"+ selectedDto.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        SocioDto sociodto = objectMapper.readValue(jsonResponse, SocioDto.class);

        assertEquals(2, sociodto.getId());

    }

    @Test
    public void testFindSocio_ById_Return404()throws Exception{

        Socio selected = new Socio(1, "77777777U", "Marcos", "García", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, null);

        when(socioService.findById(selected.getId())).thenThrow(new SocioNotFoundException("Socio con ID" + selected.getId() +" no encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.get("/socios/"+ selected.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testAddSocio_Return201() throws Exception {

        Socio socio = new Socio(2, "77777781U", "Marcos", "García", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, new ArrayList<>());

        when(socioService.add(socio)).thenReturn(socio);

        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
        String jsonRequest = om.writeValueAsString(socio);

        mockMvc.perform(MockMvcRequestBuilders.post("/socios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());

    }

    @Test
    public void testAddSocio_Return400() throws Exception {

        List<Participante>participantes = new ArrayList<>();
        Socio newsocio = new Socio(2, "777777U", "Marcos", "García", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, participantes);
        newsocio.setEmail(null);

        when(socioService.add(newsocio)).thenReturn(newsocio);

        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
        String socioJson = objectMapper.writeValueAsString(newsocio);

        mockMvc.perform(MockMvcRequestBuilders.post("/socios")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(socioJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testEditSocio_For200() throws Exception {

        List<Participante>participantes = new ArrayList<>();

        Socio originalSocio = new Socio(2, "77777781U", "Marcos", "García", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, participantes);
        Socio wantedSocio = new Socio(2, "77777781U", "Elena", "Honores", "email@email.com", "C Recogidas 90", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, participantes);

        ObjectMapper thisObjectmapper = new ObjectMapper();
        thisObjectmapper.registerModule(new JavaTimeModule());

        when(socioService.modify(2, wantedSocio)).thenReturn(wantedSocio);

        String jsonRequest = thisObjectmapper.writeValueAsString(wantedSocio);

       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/socios/" + originalSocio.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
               .andReturn();


    }


    @Test
    void testEditSocio_For400() throws Exception {

        List<Participante>participantes = new ArrayList<>();
        Socio wantedSocio =new Socio(2, "777777U", "Marcos", "García", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, participantes);

        ObjectMapper thisObjectmapper = new ObjectMapper();
        thisObjectmapper.registerModule(new JavaTimeModule());

        when(socioService.modify(2L, wantedSocio))
                .thenThrow(new SocioNotFoundException("Socio Not Found"));

        String jsonRequest = thisObjectmapper.writeValueAsString(wantedSocio);

        mockMvc.perform(MockMvcRequestBuilders.put("/socios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSocio_For204() throws Exception{
        List<Participante>participantes = new ArrayList<>();
        Socio socio = new Socio(2, "777777U", "Marcos", "García", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, participantes);

        doNothing().when(socioService).delete(socio.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/socios/" + socio.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSocio_For404() throws Exception{

        List<Participante>participantes = new ArrayList<>();
        Socio socio = new Socio(2, "777777U", "Marcos", "García", "email@email.com", "C Recogidas 128", "888-566-323", "Nuclear", true, LocalDate.now().plusDays(1), null, participantes);

        when(socioService.findById(socio.getId())).thenThrow(new SocioNotFoundException("Socio con ID" + socio.getId() +" no encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/socios/" + socio.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


}

