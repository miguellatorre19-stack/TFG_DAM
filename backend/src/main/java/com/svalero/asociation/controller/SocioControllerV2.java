package com.svalero.asociation.controller;

import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.modelv2.SocioOutDtoV2;
import com.svalero.asociation.service.ParticipanteService;
import com.svalero.asociation.service.SocioService;
import com.svalero.asociation.service.SocioServiceV2;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SocioControllerV2 {
    @Autowired
    private SocioService socioService;
    @Autowired
    private SocioServiceV2 socioServiceV2;
    @Autowired
    private ParticipanteService participanteService;

    private final Logger logger = LoggerFactory.getLogger(SocioControllerV2.class);

    @GetMapping("/v2/socios")
    public ResponseEntity<List<SocioOutDtoV2>> getAllByAllFiltersV2(
            @RequestParam(value = "familyModel", required = false) String familyModel,
            @RequestParam(value = "entryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entryDate) {

        logger.info("GET/socios");
        List<SocioOutDtoV2> allSociosDto = socioServiceV2.findAll(familyModel ,entryDate);

        if (allSociosDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allSociosDto);
    }

    @GetMapping("/v2/socios/{id}")
    public ResponseEntity<SocioDto> getSocioByIdV2(@PathVariable long id){
        logger.info("GET/socios/{id}");
        SocioDto selectedsocio = socioService.findById(id);
        if (selectedsocio == null){
            logger.warn("Socio of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(selectedsocio, HttpStatus.OK);
    }

    @PostMapping("/v2/socios")
     public ResponseEntity<Socio> addSocioV2(@Valid @RequestBody Socio socio) throws MethodArgumentNotValidException {
        logger.info("POST/socios");
        Socio newsocio = socioService.add(socio);
        return new ResponseEntity<>(newsocio, HttpStatus.CREATED);
    }

    @PutMapping("/v2/socios/{id}")
    public ResponseEntity<Socio> editSocioV2(@PathVariable long id, @Valid @RequestBody Socio socio) throws MethodArgumentNotValidException{
        logger.info("PUT/socios");
        Socio updatedsocio = socioService.modify(id, socio);
        return ResponseEntity.ok(updatedsocio);
    }

    @DeleteMapping("/v2/socios/{id}")
    public ResponseEntity<Void> deleteSocioV2(@PathVariable long id){
        logger.info("DELETE/socios");
        socioService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
