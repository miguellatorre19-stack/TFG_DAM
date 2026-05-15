package com.svalero.asociation.service;

import com.svalero.asociation.dto.TrabajadorDto;
import com.svalero.asociation.dto.TrabajadorOutDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.TrabajadorNotFoundException;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.repository.TrabajadorRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrabajadorService {

    @Autowired
    private TrabajadorRepository trabajadorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ServicioService servicioService;

    private final Logger logger = LoggerFactory.getLogger(TrabajadorService.class);

    public List<Trabajador> findAll(LocalDate entryDate, String name, String contractType){
        List<Trabajador> trabajadores = trabajadorRepository.findByFilters(entryDate, name, contractType);
        logger.info("Searching Trabajador with filters: {} {} {}", entryDate, name, contractType);
        return trabajadores;
    }

    public List<TrabajadorOutDto> findAllDto(LocalDate entryDate, String name, String contractType){
        List<Trabajador> trabajadores = findAll(entryDate, name, contractType);
        return modelMapper.map(trabajadores, new TypeToken<List<TrabajadorOutDto>>() {
        }.getType());
    }

    public Trabajador findById(long id) {
        Trabajador foundtrabajador = trabajadorRepository.findById(id).orElseThrow(()-> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));
        logger.info("Found Trabajador with ID: {} ", id);
        return foundtrabajador;
    }

    public TrabajadorOutDto findDtoById(long id) {
        Trabajador foundtrabajador = findById(id);
        return modelMapper.map(foundtrabajador, TrabajadorOutDto.class);
    }

    public Trabajador add(Trabajador trabajador, long id) {
        if(trabajadorRepository.existsBydni(trabajador.getDni())){
            logger.warn("DNI {} already exists", trabajador.getDni());
            throw new BusinessRuleException("Un trabajador con DNI " + trabajador.getDni() + " ya existe");
        }

        Servicio servicio = servicioService.findById(id);
        trabajador.setServicios(servicio);
        trabajador.setEntryDate(LocalDate.now());

        Trabajador savedTrabajador = trabajadorRepository.save(trabajador);
        logger.info("Created Trabajador with ID: {} and servicio ID: {}", savedTrabajador.getId(), id);
        return savedTrabajador;
    }

    public TrabajadorOutDto addDto(TrabajadorDto trabajadorDto, long id) {
        Trabajador trabajador = modelMapper.map(trabajadorDto, Trabajador.class);
        Trabajador savedTrabajador = add(trabajador, id);
        return modelMapper.map(savedTrabajador, TrabajadorOutDto.class);
    }

    public Trabajador modify(long id, Trabajador trabajador) {
        Trabajador oldtrabajador = trabajadorRepository.findById(id).orElseThrow(()-> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));
        LocalDate previousEntryDate = oldtrabajador.getEntryDate();

        modelMapper.map(trabajador, oldtrabajador);

        if (trabajador.getEntryDate() == null) {
            oldtrabajador.setEntryDate(previousEntryDate);
        }

        if (trabajador.getServicios() != null) {
            Servicio servicio = servicioService.findById(trabajador.getServicios().getId());
            oldtrabajador.setServicios(servicio);
        } else {
            oldtrabajador.setServicios(null);
        }

        logger.info("Updated Trabajador with ID: {} ", id);
        return trabajadorRepository.save(oldtrabajador);
    }

    public TrabajadorOutDto modifyDto(long id, TrabajadorDto trabajadorDto) {
        Trabajador trabajador = modelMapper.map(trabajadorDto, Trabajador.class);

        if (trabajadorDto.getServicioId() > 0) {
            Servicio servicio = servicioService.findById(trabajadorDto.getServicioId());
            trabajador.setServicios(servicio);
        } else {
            trabajador.setServicios(null);
        }

        Trabajador updatedTrabajador = modify(id, trabajador);
        return modelMapper.map(updatedTrabajador, TrabajadorOutDto.class);
    }

    public void delete(long id) {
        Trabajador trabajador = trabajadorRepository.findById(id).orElseThrow(()-> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));

        logger.info("Deleted Trabajador with ID: {} ", id);
        trabajadorRepository.delete(trabajador);
    }
}
