package com.svalero.asociation.service;


import com.svalero.asociation.dto.AccessCredentialsDto;
import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.ParticipanteAccessResponseDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.SocioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;
    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SocioService socioService;
    @Autowired
    private AccessUserService accessUserService;

    private final Logger logger = LoggerFactory.getLogger(ParticipanteService.class);

    public List<ParticipanteOutDto> findAll(LocalDate birthDate, String name, String typeRel){
        List<Participante> participantes = participanteRepository.findByFilters(birthDate, name, typeRel);
        logger.info("Searching with filters: {} {} {}", birthDate, name, typeRel);
        List<ParticipanteOutDto>participanteOutDtoList = modelMapper.map(participantes, new TypeToken<List<ParticipanteOutDto>>(){}.getType());
        return participanteOutDtoList;
    }

    public ParticipanteDto findById(long id) {
        Participante participanteSelected = participanteRepository.findById(id).orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + id + "no encontrado"));
        ParticipanteDto participanteDtoselected = modelMapper.map(participanteSelected, ParticipanteDto.class);

        logger.debug("Fetching participante with ID: {}", id);
        return participanteDtoselected;
    }

    public  Participante addDto(ParticipanteDto participanteDto, long id){
        Participante participante = new Participante();
        modelMapper.map(participanteDto, participante);
        if(participanteRepository.existsBydni(participante.getDni())){
            throw new BusinessRuleException("Un participante con DNI "+participante.getDni()+" ya existe");
        }
        SocioDto socioDto = socioService.findById(id);
        participante.setSocio(socioRepository.findById(socioDto.getId()).get());
        return participanteRepository.save(participante);

    }

    public ParticipanteAccessResponseDto addDtoWithAccess(ParticipanteDto participanteDto, long id) {
        Participante participante = new Participante();
        modelMapper.map(participanteDto, participante);

        if(participanteRepository.existsBydni(participante.getDni())){
            throw new BusinessRuleException("Un participante con DNI "+participante.getDni()+" ya existe");
        }

        SocioDto socioDto = socioService.findById(id);
        participante.setSocio(socioRepository.findById(socioDto.getId()).get());

        AccessCredentialsDto credentials = accessUserService.createAccessUser(
                participante.getName() + " " + participante.getSurname(),
                participante.getEmail(),
                "PARTICIPANTE"
        );

        Usuario savedUsuario = credentials.getUsuario();
        participante.setUsuario(savedUsuario);
        Participante savedParticipante = participanteRepository.save(participante);

        ParticipanteDto responseDto = modelMapper.map(savedParticipante, ParticipanteDto.class);
        responseDto.setSocioID(id);

        return new ParticipanteAccessResponseDto(
                responseDto,
                savedUsuario.getId(),
                savedUsuario.getEmail(),
                credentials.getInitialPassword()
        );
    }

    public Participante modifyDto(long id, ParticipanteDto participanteDto){
        Participante oldparticipante = participanteRepository.findById(id).orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + id + "no encontrado"));
        logger.info("Updating participante with ID: {}", id);
        modelMapper.map(participanteDto, oldparticipante);
        return participanteRepository.save(oldparticipante);
    }

    @Transactional
    public AccessCodeResponseDto regenerateAccessCode(long id) {
        Participante participante = participanteRepository.findById(id)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + id + "no encontrado"));

        AccessCredentialsDto credentials;
        if (participante.getUsuario() == null) {
            credentials = accessUserService.createAccessUser(
                    participante.getName() + " " + participante.getSurname(),
                    participante.getEmail(),
                    "PARTICIPANTE"
            );
            participante.setUsuario(credentials.getUsuario());
            participanteRepository.save(participante);
        } else {
            credentials = accessUserService.regenerateAccessCode(participante.getUsuario());
        }

        return new AccessCodeResponseDto(
                credentials.getUsuario().getId(),
                credentials.getUsuario().getEmail(),
                credentials.getInitialPassword()
        );
    }

    public void delete(long id) {
        Participante participante = participanteRepository.findById(id).orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + id + "no encontrado"));
        logger.info("Participante with ID: {} deleted successfully", id);
        participanteRepository.delete(participante);
    }
}
