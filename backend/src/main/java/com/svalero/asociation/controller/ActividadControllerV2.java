package com.svalero.asociation.controller;

import com.svalero.asociation.dto.ActividadDto;
import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.dto.InscripcionActividadRequestDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.modelv2.ActividadDtoV2;
import com.svalero.asociation.modelv2.ActividadOutDtoV2;
import com.svalero.asociation.service.ActividadService;
import com.svalero.asociation.service.ActividadServiceV2;
import com.svalero.asociation.service.InscripcionActividadService;
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
@RequestMapping("api")
public class ActividadControllerV2 {

    @Autowired
    private ActividadService actividadService;
    @Autowired
    private ActividadServiceV2 actividadServiceV2;
    @Autowired
    private InscripcionActividadService inscripcionActividadService;

    private final Logger logger = LoggerFactory.getLogger(ActividadControllerV2.class);

    @GetMapping("/v2/actividades")
    public ResponseEntity<List<ActividadOutDto>> getAllv2(
            @RequestParam(value = "dayActivity", required = false) @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate dayActivity,
            @RequestParam(value = "canJoin", required = false) Boolean canJoin,
            @RequestParam(value = "capacity", required = false) Integer capacity){
        logger.info("GET/actividades");
        List<ActividadOutDto> allactividades = actividadService.findAllv2(dayActivity, canJoin, capacity);
        return ResponseEntity.ok(allactividades);
    }

    @GetMapping("/v2/actividades/{id}")
    public ResponseEntity<ActividadOutDto> getActividadById(@PathVariable long id){
        ActividadOutDto selectedactividad = actividadService.findOutById(id);
        if (selectedactividad == null){
            logger.warn("Actividad of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET/actividades/{id}");
        return new ResponseEntity<>(selectedactividad, HttpStatus.OK);
    }

    @PostMapping("/v2/actividades")
    public ResponseEntity<ActividadOutDtoV2> addActividad(@Valid@RequestBody ActividadDtoV2 actividad) throws MethodArgumentNotValidException {
        ActividadOutDtoV2 newActividad = actividadServiceV2.add(actividad);
        logger.info("POST/actividades");
        return new ResponseEntity<>(newActividad, HttpStatus.CREATED);
    }

    @PostMapping("/v2/actividades/{id}/inscripciones")
    public ResponseEntity<Void> inscribirParticipante(@PathVariable long id, @Valid@RequestBody InscripcionActividadRequestDto requestDto)  throws MethodArgumentNotValidException  {
        inscripcionActividadService.inscribir(id, requestDto.getParticipanteId(), requestDto.getState(), requestDto.getPrice());
        logger.info("POST/actividades/{id}/inscripciones");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/v2/actividades/{idActivity}/inscripciones/{idInscripcion}")
    public ResponseEntity<Void> deleteInscripcion(@PathVariable long idActivity, @PathVariable long idInscripcion) {
        inscripcionActividadService.deleteInscripcion(idActivity, idInscripcion);
        logger.info("DELETE/actividades/{idActivity}/inscripciones/{idInscripcion}");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v2/actividades/{id}/participantes")
    public ResponseEntity<List<ParticipanteDto>> getParticipantesByActividad(@PathVariable long id) {
        List<ParticipanteDto> participantes = inscripcionActividadService.listarParticipantes(id);
        logger.info("GET/actividades/{id}/participantes");
        return ResponseEntity.ok(participantes);
    }

    @PutMapping("/v2/actividades/{id}")
    public ResponseEntity<ActividadOutDtoV2> editActividad(@PathVariable long id, @Valid@RequestBody ActividadDtoV2 actividad) throws MethodArgumentNotValidException{
        ActividadOutDtoV2 updatedactividad = actividadServiceV2.modify(id, actividad);
        if (updatedactividad == null){
            return ResponseEntity.notFound().build();
        }
        logger.info("PUT/actividades/{id}");
        return ResponseEntity.ok(updatedactividad);
    }

    @DeleteMapping("/v2/actividades/{id}")
    public ResponseEntity<Void> deleteActividad (@PathVariable long id){
        actividadService.delete(id);
        logger.info("DELETE/actividades/{id}");
        return ResponseEntity.noContent().build();
    }
}
