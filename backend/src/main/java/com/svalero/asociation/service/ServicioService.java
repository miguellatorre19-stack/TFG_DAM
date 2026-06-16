package com.svalero.asociation.service;

import com.svalero.asociation.dto.ServicioDto;
import com.svalero.asociation.dto.ServicioOutDto;
import com.svalero.asociation.exception.ServicioNotFoundException;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.repository.ServicioRepository;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(ServicioService.class);


    public List<Servicio> findAll(String periodicity, Integer capacity, Float duration, Boolean archived){
        List<Servicio> servicios = archived == null
                ? servicioRepository.findByFilters(periodicity, capacity, duration)
                : servicioRepository.findByFilters(periodicity, capacity, duration, archived);
        logger.info("Searching with filters: {} {} {} {}", periodicity, capacity, duration, archived);
        return servicios;
    }

    public List<Servicio> findAll(String periodicity, Integer capacity, Float duration){
        return findAll(periodicity, capacity, duration, null);
    }

    public List<ServicioOutDto> findAllDto(String periodicity, Integer capacity, Float duration, Boolean archived){
        List<Servicio> servicios = findAll(periodicity, capacity, duration, archived);

        return servicios.stream()
                .map(this::toOutDto)
                .toList();
    }

    public List<ServicioOutDto> findAllDto(String periodicity, Integer capacity, Float duration){
        return findAllDto(periodicity, capacity, duration, null);
    }

    private ServicioOutDto toOutDto(Servicio servicio) {
        ServicioOutDto dto = new ServicioOutDto();

        dto.setId(servicio.getId());
        dto.setDescription(servicio.getDescription());
        dto.setPeriodicity(servicio.getPeriodicity());
        dto.setRequisites(servicio.getRequisites());
        dto.setDuration(servicio.getDuration());
        dto.setCapacity(servicio.getCapacity());
        dto.setStatus(servicio.getStatus());

        return dto;
    }

    public Servicio findById(long id) {
        Servicio foundservicio = findModelById(id);
        logger.debug("Fetching servicio with ID: {}", id);
        return foundservicio;
    }

    public ServicioOutDto findDtoById(long id) {
        Servicio foundservicio = findById(id);
        return modelMapper.map(foundservicio, ServicioOutDto.class);
    }

    public Servicio add(Servicio servicio) {
        servicioRepository.save(servicio);
        logger.info("Successfully created new servicio with ID: {}", servicio.getId());
        return servicio;
    }

    public ServicioOutDto addDto(ServicioDto servicioDto) {
        Servicio servicio = modelMapper.map(servicioDto, Servicio.class);
        Servicio savedServicio = add(servicio);
        return modelMapper.map(savedServicio, ServicioOutDto.class);
    }

    public Servicio modify(long id, Servicio servicio) {
        Servicio oldservicio = servicioRepository.findById(id).orElseThrow(()-> new ServicioNotFoundException("Servicio con la ID:"+ id+ "no encontrado"));
        logger.info("Updating servicio with ID: {}", id);
        modelMapper.map(servicio, oldservicio);
        return servicioRepository.save(oldservicio);
    }

    public ServicioOutDto modifyDto(long id, ServicioDto servicioDto) {
        Servicio oldservicio = servicioRepository.findById(id).orElseThrow(()-> new ServicioNotFoundException("Servicio con la ID:"+ id+ "no encontrado"));
        logger.info("Updating servicio with ID: {}", id);
        modelMapper.map(servicioDto, oldservicio);
        Servicio updatedServicio = servicioRepository.save(oldservicio);
        return modelMapper.map(updatedServicio, ServicioOutDto.class);
    }


    @Transactional
    public void delete(long id){
        Servicio servicio = servicioRepository.findById(id).orElseThrow(()-> new ServicioNotFoundException("Servicio con la ID:"+ id+ "no encontrado"));

        if ("ARCHIVED".equalsIgnoreCase(servicio.getStatus())) {
            throw new com.svalero.asociation.exception.BusinessRuleException("El servicio con ID " + id + " ya fue archivado");
        }

        if (servicio.getTrabajadoresAsignados() != null) {
            servicio.getTrabajadoresAsignados().forEach(trabajador -> trabajador.setServicios(null));
        }
        servicio.setStatus("ARCHIVED");
        servicioRepository.save(servicio);
        logger.info("Servicio with ID: {} archived successfully", id);
    }

    public Servicio findModelById(long id) {
        return servicioRepository.findById(id).orElseThrow(()-> new ServicioNotFoundException("Servicio con la ID:"+ id+ "no encontrado"));
    }


}



