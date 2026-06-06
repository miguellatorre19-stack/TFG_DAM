package com.svalero.asociation.service;

import com.svalero.asociation.dto.AccessCredentialsDto;
import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.SocioAccessResponseDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.SocioNotFoundException;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.SocioRepository;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AccessUserService accessUserService;

    private final Logger logger = LoggerFactory.getLogger(SocioService.class);

    public List<SocioDto> findAll(String familyModel, Boolean active, LocalDate entryDate) {

        List<Socio> socios = socioRepository.findByFilters(familyModel, active, entryDate);

        logger.info("Searching with filters: {} {} {}", familyModel, active, entryDate);
        List<SocioDto> sociosDto = modelMapper.map(socios, new TypeToken<List<SocioDto>>() {
        }.getType());

        return sociosDto;
    }

    public SocioDto findById(long id) {

        Socio socioSelected = socioRepository.findById(id).orElseThrow(() -> new SocioNotFoundException("Socio con ID " + id + " no encontrado"));
        logger.debug("Fetching socio with ID: {}", id);
        SocioDto socioDto = modelMapper.map(socioSelected, SocioDto.class);
        return socioDto;
    }

    public Socio add(Socio socio) {

        if (socioRepository.existsBydni(socio.getDni())) {
            logger.warn("Failed to add socio: DNI {} already exists", socio.getDni());
            throw new BusinessRuleException("Un socio con DNI " + socio.getDni() + " ya existe");
        }
        socioRepository.save(socio);
        logger.info("Successfully created new socio with ID: {}", socio.getId());
        return socio;
    }

    @Transactional
    public SocioAccessResponseDto addWithAccess(Socio socio) {

        if (socioRepository.existsBydni(socio.getDni())) {
            logger.warn("Failed to add socio: DNI {} already exists", socio.getDni());
            throw new BusinessRuleException("Un socio con DNI " + socio.getDni() + " ya existe");
        }

        AccessCredentialsDto credentials = accessUserService.createAccessUser(
                socio.getName() + " " + socio.getSurname(),
                socio.getEmail(),
                "SOCIO"
        );

        Usuario savedUsuario = credentials.getUsuario();
        socio.setUsuario(savedUsuario);
        Socio savedSocio = socioRepository.save(socio);

        logger.info("Successfully created socio ID {} with access user ID {}", savedSocio.getId(), savedUsuario.getId());

        SocioDto socioDto = modelMapper.map(savedSocio, SocioDto.class);
        return new SocioAccessResponseDto(
                socioDto,
                savedUsuario.getId(),
                savedUsuario.getEmail(),
                credentials.getInitialPassword()
        );
    }

    public Socio modify(long id, Socio socioData) throws SocioNotFoundException {
        logger.info("Updating socio with ID: " + id);

        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new SocioNotFoundException("Socio not found"));

        socio.setName(socioData.getName());
        socio.setSurname(socioData.getSurname());
        socio.setDni(socioData.getDni());
        socio.setEmail(socioData.getEmail());
        socio.setPhoneNumber(socioData.getPhoneNumber());
        socio.setAddress(socioData.getAddress());
        socio.setEntryDate(socioData.getEntryDate());
        socio.setOutDate(socioData.getOutDate());
        socio.setFamilyModel(socioData.getFamilyModel());
        socio.setActive(socioData.getActive());

        return socioRepository.save(socio);
    }

    @Transactional
    public AccessCodeResponseDto regenerateAccessCode(long id) {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new SocioNotFoundException("Socio con ID " + id + " no encontrado"));

        AccessCredentialsDto credentials;
        if (socio.getUsuario() == null) {
            credentials = accessUserService.createAccessUser(
                    socio.getName() + " " + socio.getSurname(),
                    socio.getEmail(),
                    "SOCIO"
            );
            socio.setUsuario(credentials.getUsuario());
            socioRepository.save(socio);
        } else {
            credentials = accessUserService.regenerateAccessCode(socio.getUsuario());
        }

        return new AccessCodeResponseDto(
                credentials.getUsuario().getId(),
                credentials.getUsuario().getEmail(),
                credentials.getInitialPassword()
        );
    }

    public void delete(long id) {
        logger.info("Socio with ID: {} deleted successfully", id);
        Socio socio = socioRepository.findById(id).orElseThrow(() -> new SocioNotFoundException("Socio con ID " + id + " no encontrado"));
        socioRepository.delete(socio);
    }

}




