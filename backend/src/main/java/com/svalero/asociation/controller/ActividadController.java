package com.svalero.asociation.controller;

import com.svalero.asociation.dto.ActividadDto;
import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.dto.InscripcionActividadOutDto;
import com.svalero.asociation.dto.InscripcionActividadRequestDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.service.ActividadService;
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
@RequestMapping("/api")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;
    @Autowired
    private InscripcionActividadService inscripcionActividadService;

    private final Logger logger = LoggerFactory.getLogger(ActividadController.class);

    @GetMapping("/v1/actividades")
    public ResponseEntity<List<ActividadOutDto>> getAllv1(
            @RequestParam(value = "dayActivity", required = false) @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate dayActivity,
            @RequestParam(value = "canJoin", required = false) Boolean canJoin,
            @RequestParam(value = "duration", required = false) Float duration,
            @RequestParam(value = "archived", required = false) Boolean archived){
        logger.info("GET/actividades");
        List<ActividadOutDto> allactividades = archived == null
                ? actividadService.findAll(dayActivity, canJoin, duration)
                : actividadService.findAll(dayActivity, canJoin, duration, archived);
        return ResponseEntity.ok(allactividades);
    }


    @GetMapping("/v1/actividades/{id}")
    public ResponseEntity<ActividadOutDto> getActividadById(@PathVariable long id){
        ActividadOutDto selectedactividad = actividadService.findOutById(id);
        if (selectedactividad == null){
            logger.warn("Actividad of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET/actividades/{id}");
        return new ResponseEntity<>(selectedactividad, HttpStatus.OK);
    }

    @PostMapping("/v1/actividades")
    public ResponseEntity<ActividadOutDto> addActividad(@Valid@RequestBody ActividadDto actividad) throws MethodArgumentNotValidException {
        ActividadOutDto newActividad = actividadService.add(actividad);
        logger.info("POST/actividades");
        return new ResponseEntity<>(newActividad, HttpStatus.CREATED);
    }

    @PostMapping("/v1/actividades/{id}/inscripciones")
    public ResponseEntity<Void> inscribirParticipante(@PathVariable long id, @Valid@RequestBody InscripcionActividadRequestDto requestDto)  throws MethodArgumentNotValidException  {
        inscripcionActividadService.inscribir(id, requestDto.getParticipanteId(), requestDto.getState(), requestDto.getPrice());
        logger.info("POST/actividades/{id}/inscripciones");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/v1/actividades/{id}/participantes")
    public ResponseEntity<List<ParticipanteDto>> getParticipantesByActividad(@PathVariable long id) {
        List<ParticipanteDto> participantes = inscripcionActividadService.listarParticipantes(id);
        logger.info("GET/actividades/{id}/participantes");
        return ResponseEntity.ok(participantes);
    }

    @GetMapping("/v1/actividades/{id}/inscripciones")
    public ResponseEntity<List<InscripcionActividadOutDto>> getInscripcionesByActividad(@PathVariable long id) {
        List<InscripcionActividadOutDto> inscripciones = inscripcionActividadService.listarInscripciones(id);
        logger.info("GET/actividades/{id}/inscripciones");
        return ResponseEntity.ok(inscripciones);
    }

    @PutMapping("/v1/actividades/{idActivity}/inscripciones/{idInscripcion}")
    public ResponseEntity<InscripcionActividadOutDto> editInscripcion(@PathVariable long idActivity,
            @PathVariable long idInscripcion, @Valid @RequestBody InscripcionActividadRequestDto requestDto)
            throws MethodArgumentNotValidException {
        InscripcionActividadOutDto updatedInscripcion = inscripcionActividadService.modify(
                idActivity,
                idInscripcion,
                requestDto.getParticipanteId(),
                requestDto.getState(),
                requestDto.getPrice()
        );
        logger.info("PUT/actividades/{idActivity}/inscripciones/{idInscripcion}");
        return ResponseEntity.ok(updatedInscripcion);
    }

    @PutMapping("/v1/actividades/{id}")
    public ResponseEntity<Actividad> editActividad(@PathVariable long id, @Valid@RequestBody Actividad actividad) throws MethodArgumentNotValidException{
        Actividad updatedactividad = actividadService.modify(id, actividad);
        if (updatedactividad == null){
            return ResponseEntity.notFound().build();
        }
        logger.info("PUT/actividades/{id}");
        return ResponseEntity.ok(updatedactividad);
    }

    @DeleteMapping("/v1/actividades/{id}")
    public ResponseEntity<Void> deleteActividad (@PathVariable long id){
        actividadService.delete(id);
        logger.info("DELETE/actividades/{id}");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/actividades/{idActivity}/inscripciones/{idInscripcion}")
    public ResponseEntity<Void> deleteInscripcion(@PathVariable long idActivity, @PathVariable long idInscripcion){
        inscripcionActividadService.deleteInscripcion(idActivity, idInscripcion);
        logger.info("DELETE/actividades/{idActivity}/inscripciones/{idInscripcion}");
        return ResponseEntity.noContent().build();
    }
}
