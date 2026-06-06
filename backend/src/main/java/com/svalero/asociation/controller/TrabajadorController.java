package com.svalero.asociation.controller;

import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.TrabajadorAccessResponseDto;
import com.svalero.asociation.dto.TrabajadorDto;
import com.svalero.asociation.dto.TrabajadorOutDto;
import com.svalero.asociation.service.TrabajadorService;
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
public class TrabajadorController {
    
    @Autowired
    private TrabajadorService trabajadorService;

    private final Logger logger = LoggerFactory.getLogger(TrabajadorController.class);

    @GetMapping("/v1/trabajadores")
    public ResponseEntity<List<TrabajadorOutDto>> getAll(
            @RequestParam(value = "entryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entryDate,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "contractType", required = false) String contractType){
        List<TrabajadorOutDto> alltrabajadores = trabajadorService.findAllDto(entryDate, name, contractType);
        logger.info("GET/trabajadores");
        return ResponseEntity.ok(alltrabajadores);
    }

    @GetMapping("/v1/trabajadores/{id}")
    public ResponseEntity<TrabajadorOutDto> getTrabajadorById(@PathVariable long id){
        TrabajadorOutDto selectedtrabajador = trabajadorService.findDtoById(id);
        if (selectedtrabajador == null){
            logger.warn("Trabajador of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET/trabajadores/{id}");
        return new ResponseEntity<>(selectedtrabajador, HttpStatus.ACCEPTED);
    }

    @PostMapping("/v1/servicios/{id}/trabajadores")
    public ResponseEntity<TrabajadorAccessResponseDto> addTrabajadors(@Valid@RequestBody TrabajadorDto trabajadorDto, @PathVariable long id) throws MethodArgumentNotValidException{
        TrabajadorAccessResponseDto newtrabajador = trabajadorService.addDtoWithAccess(trabajadorDto, id);
        logger.info("POST/trabajadores");
        return new ResponseEntity<>(newtrabajador, HttpStatus.CREATED);
    }

    @PutMapping("/v1/trabajadores/{id}")
    public ResponseEntity<TrabajadorOutDto> editTrabajador(@PathVariable long id, @Valid@RequestBody TrabajadorDto trabajadorDto) throws MethodArgumentNotValidException {
        TrabajadorOutDto updatedtrabajador = trabajadorService.modifyDto(id, trabajadorDto);
        logger.info("PUT/trabajadores/{id}");
        return ResponseEntity.ok(updatedtrabajador);
    }

    @PostMapping("/v1/trabajadores/{id}/access-code")
    public ResponseEntity<AccessCodeResponseDto> regenerateTrabajadorAccessCode(@PathVariable long id) {
        logger.info("POST/trabajadores/{id}/access-code");
        AccessCodeResponseDto accessCode = trabajadorService.regenerateAccessCode(id);
        return ResponseEntity.ok(accessCode);
    }

    @DeleteMapping("/v1/trabajadores/{id}")
    public ResponseEntity<Void> deleteTrabajador (@PathVariable long id){
        trabajadorService.delete(id);
        logger.info("DELETE/trabajadores/{id}");
        return ResponseEntity.noContent().build();
    }

}
