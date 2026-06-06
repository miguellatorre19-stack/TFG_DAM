package com.svalero.asociation.controller;

import com.svalero.asociation.dto.SocioAccessResponseDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.service.ParticipanteService;
import com.svalero.asociation.service.SocioService;
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
public class SocioController {
    @Autowired
    private SocioService socioService;
    @Autowired
    private ParticipanteService participanteService;

    private final Logger logger = LoggerFactory.getLogger(SocioController.class);

    @GetMapping("/v1/socios")
    public ResponseEntity<List<SocioDto>> getAllByAllFilters(
            @RequestParam(value = "familyModel", required = false) String familyModel,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "entryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entryDate) {

        logger.info("GET/socios");
        List<SocioDto> allSociosDto = socioService.findAll(familyModel, active, entryDate);

        if (allSociosDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(allSociosDto);
    }

    @GetMapping("/v1/socios/{id}")
    public ResponseEntity<SocioDto> getSocioById(@PathVariable long id){
        logger.info("GET/socios/{id}");
        SocioDto selectedsocio = socioService.findById(id);
        if (selectedsocio == null){
            logger.warn("Socio of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(selectedsocio, HttpStatus.OK);
    }

    @PostMapping("/v1/socios")
     public ResponseEntity<SocioAccessResponseDto> addSocio(@Valid @RequestBody Socio socio) throws MethodArgumentNotValidException {
        logger.info("POST/socios");
        SocioAccessResponseDto newSocio = socioService.addWithAccess(socio);
        return new ResponseEntity<>(newSocio, HttpStatus.CREATED);
    }

    @PutMapping("/v1/socios/{id}")
    public ResponseEntity<Socio> editSocio(@PathVariable long id, @Valid @RequestBody Socio socio) throws MethodArgumentNotValidException{
        logger.info("PUT/socios");
        Socio updatedsocio = socioService.modify(id, socio);
        return ResponseEntity.ok(updatedsocio);
    }

    @DeleteMapping("/v1/socios/{id}")
    public ResponseEntity<Void> deleteSocio(@PathVariable long id){
        logger.info("DELETE/socios");
        socioService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
