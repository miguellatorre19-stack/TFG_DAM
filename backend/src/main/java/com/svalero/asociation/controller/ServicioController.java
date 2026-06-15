package com.svalero.asociation.controller;

import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.SolicitudServicioOutDto;
import com.svalero.asociation.dto.SolicitudServicioRequestDto;
import com.svalero.asociation.dto.ServicioDto;
import com.svalero.asociation.dto.ServicioOutDto;
import com.svalero.asociation.service.SolicitudServicioService;
import com.svalero.asociation.service.ServicioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServicioController {
    @Autowired
    private ServicioService servicioService;
    @Autowired
    private SolicitudServicioService solicitudServicioService;

    private final Logger logger = LoggerFactory.getLogger(ServicioController.class);


    @GetMapping("/v1/servicios")
    public ResponseEntity<List<ServicioOutDto>> getAll(
            @RequestParam(value = "periodicity", required = false)String periodicity,
            @RequestParam(value="capacity", required = false) Integer capacity,
            @RequestParam(value="duration", required = false) Float duration ){
        List<ServicioOutDto> allservicios = servicioService.findAllDto(periodicity, capacity, duration);
        logger.info("GET/servicios");
        return ResponseEntity.ok(allservicios);
    }

    @GetMapping("/v1/servicios/{id}")
    public ResponseEntity<ServicioOutDto> getServicioById(@PathVariable long id){
        ServicioOutDto selectedservicio = servicioService.findDtoById(id);
        if (selectedservicio == null){
            logger.warn("Servicio of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET/servicios/{id}");
        return new ResponseEntity<>(selectedservicio, HttpStatus.ACCEPTED);
    }

    @PostMapping("/v1/servicios")
    public ResponseEntity<ServicioOutDto> addServicio(@Valid@RequestBody ServicioDto servicioDto) throws MethodArgumentNotValidException {
        ServicioOutDto newservicio = servicioService.addDto(servicioDto);
        logger.info("POST/servicios");
        return new ResponseEntity<>(newservicio, HttpStatus.CREATED);
    }

    @PostMapping("/v1/servicios/{id}/solicitudes")
    public ResponseEntity<Void> crearSolicitud(@PathVariable long id,
            @Valid @RequestBody SolicitudServicioRequestDto requestDto) throws MethodArgumentNotValidException {
        solicitudServicioService.crearSolicitud(id, requestDto.getParticipanteId(), requestDto.getState(), requestDto.getPrice());
        logger.info("POST/servicios/{id}/solicitudes");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/v1/servicios/{id}/participantes")
    public ResponseEntity<List<ParticipanteDto>> getParticipantesByServicio(@PathVariable long id) {
        List<ParticipanteDto> participantes = solicitudServicioService.listarParticipantes(id);
        logger.info("GET/servicios/{id}/participantes");
        return ResponseEntity.ok(participantes);
    }

    @GetMapping("/v1/servicios/{id}/solicitudes")
    public ResponseEntity<List<SolicitudServicioOutDto>> getSolicitudesByServicio(@PathVariable long id) {
        List<SolicitudServicioOutDto> solicitudes = solicitudServicioService.listarSolicitudes(id);
        logger.info("GET/servicios/{id}/solicitudes");
        return ResponseEntity.ok(solicitudes);
    }

    @PutMapping("/v1/servicios/{id}")
    public ResponseEntity<ServicioOutDto> editServicio(@PathVariable long id, @Valid @RequestBody ServicioDto servicioDto) throws MethodArgumentNotValidException{
        ServicioOutDto updatedservicio = servicioService.modifyDto(id, servicioDto);
        logger.info("PUT/servicios/{id}");
        return ResponseEntity.ok(updatedservicio);
    }

    @PutMapping("/v1/servicios/{idServicio}/solicitudes/{idSolicitud}")
    public ResponseEntity<SolicitudServicioOutDto> editarSolicitud(@PathVariable long idServicio,
            @PathVariable long idSolicitud, @Valid @RequestBody SolicitudServicioRequestDto requestDto)
            throws MethodArgumentNotValidException {
        SolicitudServicioOutDto updatedSolicitud = solicitudServicioService.modificarSolicitud(
                idServicio,
                idSolicitud,
                requestDto.getParticipanteId(),
                requestDto.getState(),
                requestDto.getPrice()
        );
        logger.info("PUT/servicios/{idServicio}/solicitudes/{idSolicitud}");
        return ResponseEntity.ok(updatedSolicitud);
    }

    @DeleteMapping("/v1/servicios/{id}")
    public ResponseEntity<Void> deleteServicio (@PathVariable long id){
        servicioService.delete(id);
        logger.info("DELETE/servicios/{id}");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/servicios/{idServicio}/solicitudes/{idSolicitud}")
    public ResponseEntity<Void> cancelarSolicitud(@PathVariable long idServicio, @PathVariable long idSolicitud){
        solicitudServicioService.cancelarSolicitud(idServicio, idSolicitud);
        logger.info("DELETE/servicios/{idServicio}/solicitudes/{idSolicitud}");
        return ResponseEntity.noContent().build();
    }


}
