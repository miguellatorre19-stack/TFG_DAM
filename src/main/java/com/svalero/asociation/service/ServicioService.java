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

import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(ServicioService.class);


    public List<Servicio> findAll(String periodicity, Integer capacity, Float duration){
        List<Servicio> servicios = servicioRepository.findByFilters(periodicity, capacity, duration);
        logger.info("Searching with filters: {} {} {}", periodicity, capacity, duration);
        return servicios;
    }

    public List<ServicioOutDto> findAllDto(String periodicity, Integer capacity, Float duration){
        List<Servicio> servicios = findAll(periodicity, capacity, duration);
        return modelMapper.map(servicios, new TypeToken<List<ServicioOutDto>>() {
        }.getType());
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


    public void delete(long id){
        Servicio servicio = servicioRepository.findById(id).orElseThrow(()-> new ServicioNotFoundException("Servicio con la ID:"+ id+ "no encontrado"));
        logger.info("Servicio with ID: {} deleted successfully", id);
        servicioRepository.delete(servicio);
    }

    public Servicio findModelById(long id) {
        return servicioRepository.findById(id).orElseThrow(()-> new ServicioNotFoundException("Servicio con la ID:"+ id+ "no encontrado"));
    }


}



