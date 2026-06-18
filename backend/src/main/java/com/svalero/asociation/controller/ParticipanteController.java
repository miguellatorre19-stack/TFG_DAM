package com.svalero.asociation.controller;

import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.BajaRequestDto;
import com.svalero.asociation.dto.ParticipanteAccessResponseDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.exception.SocioNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.service.ParticipanteService;
import com.svalero.asociation.service.SocioService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;
    private final Logger logger = LoggerFactory.getLogger(ParticipanteController.class);
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/v1/participantes")
    public ResponseEntity<List<ParticipanteOutDto>> getAll(
            @RequestParam(value = "birthDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "typeRel",required = false) String typeRel,
            @RequestParam(value = "active", required = false) Boolean active)
    {
        List<ParticipanteOutDto> allparticipantes = active == null
                ? participanteService.findAll(birthDate, name, typeRel)
                : participanteService.findAll(birthDate, name, typeRel, active);

        logger.info("GET/participantes");
        return ResponseEntity.ok(allparticipantes);
    }

    @GetMapping("/v1/participantes/{id}")
    public ResponseEntity<ParticipanteDto> getParticipanteById(@PathVariable long id) {

        ParticipanteDto selectedparticipante = participanteService.findById(id);
        if (selectedparticipante == null){
            logger.warn("Participante of ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET/participantes/{id}");
        return new ResponseEntity<>(selectedparticipante, HttpStatus.OK);
    }

    @PostMapping("/v1/socios/{id}/participante")
    public ResponseEntity<ParticipanteAccessResponseDto> addParticipante(@Valid@RequestBody ParticipanteDto participanteDto, @PathVariable long id) throws SocioNotFoundException, ParticipanteNotFoundException, MethodArgumentNotValidException {

        ParticipanteAccessResponseDto newparticipante = participanteService.addDtoWithAccess(participanteDto, id);
        return new ResponseEntity<>(newparticipante, HttpStatus.CREATED);
    }

    @PutMapping("/v1/participantes/{id}")
    public ResponseEntity<ParticipanteDto> editParticipante(@PathVariable long id, @Valid@RequestBody ParticipanteDto participanteDto) throws MethodArgumentNotValidException{

        Participante updatedparticipante = participanteService.modifyDto(id, participanteDto);
        ParticipanteDto participanteDtoUpdated = new ParticipanteDto();
        participanteDtoUpdated.setDni(updatedparticipante.getDni());
        participanteDtoUpdated.setName(updatedparticipante.getName());
        participanteDtoUpdated.setSurname(updatedparticipante.getSurname());
        participanteDtoUpdated.setEmail(updatedparticipante.getEmail());
        participanteDtoUpdated.setPhoneNumber(updatedparticipante.getPhoneNumber());
        participanteDtoUpdated.setBirthDate(updatedparticipante.getBirthDate());
        participanteDtoUpdated.setNeeds(updatedparticipante.getNeeds());
        participanteDtoUpdated.setTypeRel(updatedparticipante.getTypeRel());
        participanteDtoUpdated.setSocioID(participanteDto.getSocioID());
        logger.info("PUT/participantes/{id}");
        return ResponseEntity.ok(participanteDtoUpdated);
    }

    @PostMapping("/v1/participantes/{id}/access-code")
    public ResponseEntity<AccessCodeResponseDto> regenerateParticipanteAccessCode(@PathVariable long id) {
        logger.info("POST/participantes/{id}/access-code");
        AccessCodeResponseDto accessCode = participanteService.regenerateAccessCode(id);
        return ResponseEntity.ok(accessCode);
    }

    @PostMapping("/v1/participantes/{id}/baja")
    public ResponseEntity<Void> bajaParticipante(@PathVariable long id, @Valid @RequestBody BajaRequestDto bajaRequestDto) {
        participanteService.darDeBaja(id, bajaRequestDto);
        logger.info("POST/participantes/{id}/baja");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/participantes/{id}/reactivar")
    public ResponseEntity<Void> reactivarParticipante(@PathVariable long id) {
        participanteService.reactivar(id);
        logger.info("POST/participantes/{id}/reactivar");
        return ResponseEntity.noContent().build();
    }
}
