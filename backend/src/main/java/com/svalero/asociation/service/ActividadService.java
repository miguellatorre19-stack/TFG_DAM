package com.svalero.asociation.service;

import com.svalero.asociation.dto.ActividadDto;
import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.exception.ActividadNotFoundException;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.repository.ActividadRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(ActividadService.class);

    public List<ActividadOutDto> findAll(LocalDate dayActivity, Boolean canJoin, Float duration, Boolean archived) {
        List<Actividad> actividades = archived == null
                ? actividadRepository.findByFilters(dayActivity, canJoin, duration)
                : actividadRepository.findByFilters(dayActivity, canJoin, duration, archived);
        logger.info("Searching Actividad with filters: {} {} {} {}", dayActivity, canJoin, duration, archived);

        return actividades.stream()
                .map(this::toOutDto)
                .toList();
    }

    public List<ActividadOutDto> findAll(LocalDate dayActivity, Boolean canJoin, Float duration) {
        return findAll(dayActivity, canJoin, duration, null);
    }

    private ActividadOutDto toOutDto(Actividad actividad) {
        ActividadOutDto dto = new ActividadOutDto();

        dto.setId(actividad.getId());
        dto.setDescription(actividad.getDescription());
        dto.setDayActivity(actividad.getDayActivity());
        dto.setTypeActivity(actividad.getTypeActivity());
        dto.setDuration(actividad.getDuration());
        dto.setCanJoin(actividad.getCanJoin());
        dto.setCapacity(actividad.getCapacity());
        dto.setStatus(actividad.getStatus());

        return dto;
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

    public ActividadOutDto add(ActividadDto actividadDto) {
        Actividad actividad = modelMapper.map(actividadDto, Actividad.class);
        actividad = actividadRepository.save(actividad);
        logger.info("Successfully created new socio with ID: {}", actividad.getId());
        return modelMapper.map(actividad, ActividadOutDto.class);
    }

    public Actividad modify(long id, Actividad actividad) {
        Actividad oldactividad = actividadRepository.findById(id).orElseThrow(() -> new ActividadNotFoundException("Actividad con ID:" + id + "not found"));
        logger.info("Updating socio with ID: {}", id);
        modelMapper.map(actividad, oldactividad);
        return actividadRepository.save(oldactividad);
    }

    public void delete(long id) {
        Actividad actividad = actividadRepository.findById(id).orElseThrow(() -> new ActividadNotFoundException("Actividad con ID:" + id + "not found"));

        if ("ARCHIVED".equalsIgnoreCase(actividad.getStatus())) {
            throw new com.svalero.asociation.exception.BusinessRuleException("La actividad con ID " + id + " ya fue archivada");
        }

        actividad.setStatus("ARCHIVED");
        actividadRepository.save(actividad);
        logger.info("Actividad with ID: {} archived successfully", id);
    }
}
