package com.svalero.asociation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svalero.asociation.dto.MeResponseDto;
import com.svalero.asociation.dto.PrivateAreaRequestDto;
import com.svalero.asociation.service.MeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MeService meService;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetCurrentUserReturns200() throws Exception {
        MeResponseDto response = new MeResponseDto(
                7L,
                "Ana",
                "ana@example.com",
                Set.of("PARTICIPANTE"),
                "PARTICIPANTE",
                3L,
                11L,
                List.of(11L),
                null
        );
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("ana@example.com", "secret");

        when(meService.getCurrentUser("ana@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/v1/me")
                        .principal(authentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.profileType").value("PARTICIPANTE"))
                .andExpect(jsonPath("$.participanteIds", hasSize(1)))
                .andExpect(jsonPath("$.participanteIds[0]").value(11));

        verify(meService).getCurrentUser("ana@example.com");
    }

    @Test
    void testGetCurrentRequestsReturns200() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("ana@example.com", "secret");

        List<PrivateAreaRequestDto> requests = List.of(
                new PrivateAreaRequestDto(3L, "actividad", 8L, "Teatro", LocalDate.of(2026, 6, 18), "ENVIADA", 0f, 11L),
                new PrivateAreaRequestDto(4L, "servicio", 5L, "Logopedia", LocalDate.of(2026, 6, 17), "PENDING", 0f, 11L)
        );

        when(meService.getCurrentRequests("ana@example.com")).thenReturn(requests);

        mockMvc.perform(get("/api/v1/me/requests")
                        .principal(authentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].kind").value("actividad"))
                .andExpect(jsonPath("$[0].title").value("Teatro"))
                .andExpect(jsonPath("$[1].kind").value("servicio"))
                .andExpect(jsonPath("$[1].participanteId").value(11));

        verify(meService).getCurrentRequests("ana@example.com");
    }
}
