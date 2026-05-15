package com.svalero.asociation.controller;

import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.exception.SocioNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.service.ParticipanteService;
import com.svalero.asociation.service.ParticipanteServiceV2;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api")
public class ParticipanteControllerV2 {

    @Autowired
    private ParticipanteService participanteService;
    @Autowired
    private ParticipanteServiceV2 participanteServiceV2;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(ParticipanteControllerV2.class);

    @GetMapping("/v2/participantes")
    public ResponseEntity<List<ParticipanteOutDto>> getAll(
            @RequestParam(value = "birthDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "typeRel",required = false) String typeRel)
    {
        List<ParticipanteOutDto> allparticipantes = participanteService.findAll(birthDate, name, typeRel);

        logger.info("GET/participantes");
        return ResponseEntity.ok(allparticipantes);
    }

    @GetMapping("/v2/participantes/{id}")
    public ResponseEntity<ParticipanteDto> getParticipanteById(@PathVariable long id) {

        ParticipanteDto selectedparticipante = participanteService.findById(id);
        if (selectedparticipante == null){
            logger.warn("Participante of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET/participantes/{id}");
        return new ResponseEntity<>(selectedparticipante, HttpStatus.OK);
    }

    @PostMapping("/v2/socios/{id}/participante")
    public ResponseEntity<ParticipanteDto> addParticipante(@Valid@RequestBody ParticipanteDto participanteDto, @PathVariable long id) throws SocioNotFoundException, ParticipanteNotFoundException, MethodArgumentNotValidException {

        Participante newparticipante = participanteService.addDto(participanteDto, id);
        ParticipanteDto participanteDtoFinal = modelMapper.map(newparticipante, ParticipanteDto.class);
        participanteDtoFinal.setSocioID(participanteDto.getSocioID());
        return new ResponseEntity<>(participanteDtoFinal, HttpStatus.CREATED);
    }

    @PutMapping("/v2/participantes/{id}")
    public ResponseEntity<ParticipanteDto> editParticipante(@PathVariable long id, @Valid@RequestBody ParticipanteDto participanteDto) throws MethodArgumentNotValidException{

        Participante updatedparticipante = participanteService.modifyDto(id, participanteDto);
        ParticipanteDto participanteDtoUpdated = modelMapper.map(updatedparticipante, ParticipanteDto.class);
        participanteDtoUpdated.setSocioID(participanteDto.getSocioID());
        logger.info("PUT/participantes/{id}");
        return ResponseEntity.ok(participanteDtoUpdated);
    }

    @DeleteMapping("/v2/participantes/{id}")
    public ResponseEntity<?> deleteParticipante (@PathVariable long id,
                                                 @RequestParam (required = false) String reason){
        participanteServiceV2.delete(id, "");
        logger.info("DELETE/participantes/{id}");
        return ResponseEntity.ok(Map.of(
                "message" , "Dado de baja correctamente",
                "id: ", id,
                "fecha", LocalDate.now(),
                "reason", reason
        ));
    }
}
