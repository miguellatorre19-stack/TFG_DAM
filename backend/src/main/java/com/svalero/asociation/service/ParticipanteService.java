package com.svalero.asociation.service;


import com.svalero.asociation.dto.AccessCredentialsDto;
import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.ParticipanteAccessResponseDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.ParticipanteNotFoundException;
import com.svalero.asociation.exception.SocioNotFoundException;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.InscripcionActividadRepository;
import com.svalero.asociation.repository.InscripcionServicioRepository;
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
    private InscripcionActividadRepository inscripcionActividadRepository;
    @Autowired
    private InscripcionServicioRepository inscripcionServicioRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AccessUserService accessUserService;

    private final Logger logger = LoggerFactory.getLogger(ParticipanteService.class);

    public List<ParticipanteOutDto> findAll(LocalDate birthDate, String name, String typeRel){
        List<Participante> participantes = participanteRepository.findByFilters(birthDate, name, typeRel);
        logger.info("Searching with filters: {} {} {}", birthDate, name, typeRel);
        return participantes.stream()
                .map(this::toOutDto)
                .toList();
    }

    public ParticipanteDto findById(long id) {
        Participante participanteSelected = participanteRepository.findById(id).orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + id + "no encontrado"));

        logger.debug("Fetching participante with ID: {}", id);
        return toDto(participanteSelected);
    }

    public  Participante addDto(ParticipanteDto participanteDto, long id){
        Participante participante = new Participante();
        modelMapper.map(participanteDto, participante);
        if(participanteRepository.existsBydni(participante.getDni())){
            throw new BusinessRuleException("Un participante con DNI "+participante.getDni()+" ya existe");
        }
        participante.setSocio(socioRepository.findById(id)
                .orElseThrow(() -> new SocioNotFoundException("Socio con ID:" + id + " no encontrado")));
        return participanteRepository.save(participante);

    }

    public ParticipanteAccessResponseDto addDtoWithAccess(ParticipanteDto participanteDto, long id) {
        Participante participante = new Participante();
        modelMapper.map(participanteDto, participante);

        if(participanteRepository.existsBydni(participante.getDni())){
            throw new BusinessRuleException("Un participante con DNI "+participante.getDni()+" ya existe");
        }

        participante.setSocio(socioRepository.findById(id)
                .orElseThrow(() -> new SocioNotFoundException("Socio con ID:" + id + " no encontrado")));

        AccessCredentialsDto credentials = accessUserService.createAccessUser(
                participante.getName() + " " + participante.getSurname(),
                participante.getEmail(),
                "PARTICIPANTE"
        );

        Usuario savedUsuario = credentials.getUsuario();
        participante.setUsuario(savedUsuario);
        Participante savedParticipante = participanteRepository.save(participante);

        return new ParticipanteAccessResponseDto(
                toDto(savedParticipante),
                savedUsuario.getId(),
                savedUsuario.getEmail(),
                credentials.getInitialPassword()
        );
    }

    public Participante modifyDto(long id, ParticipanteDto participanteDto){
        Participante oldparticipante = participanteRepository.findById(id).orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + id + "no encontrado"));
        logger.info("Updating participante with ID: {}", id);
        modelMapper.map(participanteDto, oldparticipante);

        if (participanteDto.getSocioID() > 0
                && (oldparticipante.getSocio() == null
                || oldparticipante.getSocio().getId() != participanteDto.getSocioID())) {
            oldparticipante.setSocio(socioRepository.findById(participanteDto.getSocioID())
                    .orElseThrow(() -> new SocioNotFoundException("Socio con ID:" + participanteDto.getSocioID() + " no encontrado")));
        } else if (participanteDto.getSocioID() <= 0) {
            oldparticipante.setSocio(null);
        }

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

    @Transactional
    public void delete(long id) {
        Participante participante = participanteRepository.findById(id).orElseThrow(() -> new ParticipanteNotFoundException("Participante con ID:" + id + "no encontrado"));

        if (inscripcionActividadRepository.existsByParticipanteId(id)) {
            inscripcionActividadRepository.deleteByParticipanteId(id);
        }

        if (inscripcionServicioRepository.existsByParticipanteId(id)) {
            inscripcionServicioRepository.deleteByParticipanteId(id);
        }

        participanteRepository.delete(participante);
        accessUserService.deleteAccessUser(participante.getUsuario());
        logger.info("Participante with ID: {} deleted successfully", id);
    }

    private ParticipanteDto toDto(Participante participante) {
        ParticipanteDto dto = new ParticipanteDto();
        dto.setDni(participante.getDni());
        dto.setName(participante.getName());
        dto.setSurname(participante.getSurname());
        dto.setEmail(participante.getEmail());
        dto.setPhoneNumber(participante.getPhoneNumber());
        dto.setBirthDate(participante.getBirthDate());
        dto.setNeeds(participante.getNeeds());
        dto.setTypeRel(participante.getTypeRel());
        dto.setSocioID(participante.getSocio() != null ? participante.getSocio().getId() : 0);
        return dto;
    }

    private ParticipanteOutDto toOutDto(Participante participante) {
        ParticipanteOutDto dto = new ParticipanteOutDto();
        dto.setId(participante.getId());
        dto.setDni(participante.getDni());
        dto.setName(participante.getName());
        dto.setSurname(participante.getSurname());
        dto.setEmail(participante.getEmail());
        dto.setPhoneNumber(participante.getPhoneNumber());
        dto.setBirthDate(participante.getBirthDate());
        dto.setNeeds(participante.getNeeds());
        dto.setTypeRel(participante.getTypeRel());
        dto.setSocioID(participante.getSocio() != null ? participante.getSocio().getId() : 0);
        return dto;
    }
}
