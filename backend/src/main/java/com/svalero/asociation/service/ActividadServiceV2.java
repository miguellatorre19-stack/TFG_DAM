package com.svalero.asociation.service;

import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.exception.ActividadNotFoundException;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.modelv2.ActividadDtoV2;
import com.svalero.asociation.modelv2.ActividadOutDtoV2;
import com.svalero.asociation.repository.ActividadRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActividadServiceV2 {

    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(ActividadServiceV2.class);

    public List<ActividadOutDto> findAllv2(LocalDate dayActivity, Boolean canJoin, Integer capacity) {
        List<Actividad> actividades = actividadRepository.findByFiltersv2(dayActivity, canJoin, capacity);
        logger.info("Searching Actividad with filters: {} {} {}", dayActivity, canJoin, capacity);
        List<ActividadOutDto> actividadOutDtoList = modelMapper.map(actividades, new TypeToken<List<ActividadOutDto>>(){}.getType());
        return actividadOutDtoList;
    }

    public Actividad findById(long id) {
        Actividad foundactividad = actividadRepository.findById(id).orElseThrow(() -> new ActividadNotFoundException("Actividad con ID:" + id + "not found"));
        logger.debug("Fetching actividad with ID: {}", id);
        return foundactividad;
    }

    public ActividadOutDto findOutById(long id) {
        Actividad foundactividad = findById(id);
        return modelMapper.map(foundactividad, ActividadOutDto.class);
    }

    public ActividadOutDtoV2 add(ActividadDtoV2 actividadDto) {
        Actividad actividad = modelMapper.map(actividadDto, Actividad.class);
        actividad = actividadRepository.save(actividad);
        logger.info("Successfully created new socio with ID: {}", actividad.getId());
        return modelMapper.map(actividad, ActividadOutDtoV2.class);
    }

    public ActividadOutDtoV2 modify(long id, ActividadDtoV2 actividad) {
        Actividad oldactividad = actividadRepository.findById(id).orElseThrow(() -> new ActividadNotFoundException("Actividad con ID:" + id + "not found"));
        logger.info("Updating socio with ID: {}", id);
        modelMapper.map(actividad, oldactividad);
        actividadRepository.save(oldactividad);
        return modelMapper.map(oldactividad, ActividadOutDtoV2.class);
    }

    public void delete(long id) {
        Actividad actividad = actividadRepository.findById(id).orElseThrow(() -> new ActividadNotFoundException("Actividad con ID:" + id + "not found"));
        logger.info("Socio with ID: {} deleted successfully", id);
        actividadRepository.delete(actividad);
    }

}
