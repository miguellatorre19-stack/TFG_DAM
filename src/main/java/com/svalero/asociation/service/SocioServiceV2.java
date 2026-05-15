package com.svalero.asociation.service;

import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.SocioNotFoundException;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.modelv2.SocioOutDtoV2;
import com.svalero.asociation.repository.SocioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class SocioServiceV2 {

    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(SocioServiceV2.class);

    public List<SocioOutDtoV2> findAll(String familyModel, LocalDate entryDate) {

        List<Socio> socios = socioRepository.findByFiltersV2(familyModel,  entryDate);

        logger.info("Searching with filters: {} {} {}", familyModel, entryDate);
        return modelMapper.map(socios, new TypeToken<List<SocioOutDtoV2>>() {
        }.getType());
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

    public Socio modify(long id, Socio socio) {
        Socio oldsocio = socioRepository.findById(id).orElseThrow(() -> new SocioNotFoundException("Socio con ID " + id + " no encontrado"));
        logger.info("Updating socio with ID: {}", id);
        modelMapper.map(socio, oldsocio);
        return socioRepository.save(oldsocio);
    }

    public void delete(long id) {
        logger.info("Socio with ID: {} deleted successfully", id);
        Socio socio = socioRepository.findById(id).orElseThrow(() -> new SocioNotFoundException("Socio con ID " + id + " no encontrado"));
        socioRepository.delete(socio);
    }

}




