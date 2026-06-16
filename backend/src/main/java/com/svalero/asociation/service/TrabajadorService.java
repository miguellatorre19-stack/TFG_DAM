package com.svalero.asociation.service;

import com.svalero.asociation.dto.AccessCredentialsDto;
import com.svalero.asociation.dto.AccessCodeResponseDto;
import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.dto.BajaRequestDto;
import com.svalero.asociation.dto.TrabajadorAccessResponseDto;
import com.svalero.asociation.dto.TrabajadorDto;
import com.svalero.asociation.dto.TrabajadorOutDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.exception.TrabajadorNotFoundException;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.TrabajadorRepository;
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
public class TrabajadorService {

    @Autowired
    private TrabajadorRepository trabajadorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ServicioService servicioService;
    @Autowired
    private ActividadService actividadService;
    @Autowired
    private AccessUserService accessUserService;

    private final Logger logger = LoggerFactory.getLogger(TrabajadorService.class);

    public List<Trabajador> findAll(LocalDate entryDate, String name, String contractType, Boolean active){
        List<Trabajador> trabajadores = active == null
                ? trabajadorRepository.findByFilters(entryDate, name, contractType)
                : trabajadorRepository.findByFilters(entryDate, name, contractType, active);
        logger.info("Searching Trabajador with filters: {} {} {} {}", entryDate, name, contractType, active);
        return trabajadores;
    }

    public List<Trabajador> findAll(LocalDate entryDate, String name, String contractType){
        return findAll(entryDate, name, contractType, null);
    }

    public List<TrabajadorOutDto> findAllDto(LocalDate entryDate, String name, String contractType, Boolean active){
        List<Trabajador> trabajadores = findAll(entryDate, name, contractType, active);
        return trabajadores.stream()
                .map(this::toOutDto)
                .toList();
    }

    public List<TrabajadorOutDto> findAllDto(LocalDate entryDate, String name, String contractType){
        return findAllDto(entryDate, name, contractType, null);
    }

    public Trabajador findById(long id) {
        Trabajador foundtrabajador = trabajadorRepository.findById(id).orElseThrow(()-> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));
        logger.info("Found Trabajador with ID: {} ", id);
        return foundtrabajador;
    }

    public TrabajadorOutDto findDtoById(long id) {
        Trabajador foundtrabajador = findById(id);
        return toOutDto(foundtrabajador);
    }

    public Trabajador add(Trabajador trabajador, long id) {
        if(trabajadorRepository.existsBydni(trabajador.getDni())){
            logger.warn("DNI {} already exists", trabajador.getDni());
            throw new BusinessRuleException("Un trabajador con DNI " + trabajador.getDni() + " ya existe");
        }

        Servicio servicio = servicioService.findById(id);
        validateServicioAsignable(servicio);
        trabajador.setServicios(servicio);
        if (trabajador.getActividad() != null) {
            Actividad actividad = actividadService.findById(trabajador.getActividad().getId());
            validateActividadAsignable(actividad);
            trabajador.setActividad(actividad);
        }
        trabajador.setEntryDate(LocalDate.now());

        Trabajador savedTrabajador = trabajadorRepository.save(trabajador);
        logger.info("Created Trabajador with ID: {} and servicio ID: {}", savedTrabajador.getId(), id);
        return savedTrabajador;
    }

    public TrabajadorOutDto addDto(TrabajadorDto trabajadorDto, long id) {
        Trabajador trabajador = modelMapper.map(trabajadorDto, Trabajador.class);
        if (trabajadorDto.getActividadId() > 0) {
            Actividad actividad = actividadService.findById(trabajadorDto.getActividadId());
            validateActividadAsignable(actividad);
            trabajador.setActividad(actividad);
        }
        Trabajador savedTrabajador = add(trabajador, id);
        return modelMapper.map(savedTrabajador, TrabajadorOutDto.class);
    }

    public TrabajadorAccessResponseDto addDtoWithAccess(TrabajadorDto trabajadorDto, long id) {
        Trabajador trabajador = modelMapper.map(trabajadorDto, Trabajador.class);

        if(trabajadorRepository.existsBydni(trabajador.getDni())){
            logger.warn("DNI {} already exists", trabajador.getDni());
            throw new BusinessRuleException("Un trabajador con DNI " + trabajador.getDni() + " ya existe");
        }

        Servicio servicio = servicioService.findById(id);
        validateServicioAsignable(servicio);
        trabajador.setServicios(servicio);
        if (trabajadorDto.getActividadId() > 0) {
            Actividad actividad = actividadService.findById(trabajadorDto.getActividadId());
            validateActividadAsignable(actividad);
            trabajador.setActividad(actividad);
        } else {
            trabajador.setActividad(null);
        }
        trabajador.setEntryDate(LocalDate.now());

        AccessCredentialsDto credentials = accessUserService.createAccessUser(
                trabajador.getName() + " " + trabajador.getSurname(),
                trabajador.getEmail(),
                "TRABAJADOR"
        );

        Usuario savedUsuario = credentials.getUsuario();
        trabajador.setUsuario(savedUsuario);
        Trabajador savedTrabajador = trabajadorRepository.save(trabajador);

        return new TrabajadorAccessResponseDto(
                toOutDto(savedTrabajador),
                savedUsuario.getId(),
                savedUsuario.getEmail(),
                credentials.getInitialPassword()
        );
    }

    public Trabajador modify(long id, Trabajador trabajador) {
        Trabajador oldtrabajador = trabajadorRepository.findById(id).orElseThrow(()-> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));
        oldtrabajador.setDni(trabajador.getDni());
        oldtrabajador.setName(trabajador.getName());
        oldtrabajador.setSurname(trabajador.getSurname());
        oldtrabajador.setEmail(trabajador.getEmail());
        oldtrabajador.setPhoneNumber(trabajador.getPhoneNumber());
        oldtrabajador.setBirthDate(trabajador.getBirthDate());
        oldtrabajador.setContractType(trabajador.getContractType());
        if (trabajador.getEntryDate() != null) {
            oldtrabajador.setEntryDate(trabajador.getEntryDate());
        }

        if (trabajador.getServicios() != null) {
            Servicio servicio = servicioService.findById(trabajador.getServicios().getId());
            validateServicioAsignable(servicio);
            oldtrabajador.setServicios(servicio);
        } else {
            oldtrabajador.setServicios(null);
        }

        if (trabajador.getActividad() != null) {
            Actividad actividad = actividadService.findById(trabajador.getActividad().getId());
            validateActividadAsignable(actividad);
            oldtrabajador.setActividad(actividad);
        } else {
            oldtrabajador.setActividad(null);
        }

        logger.info("Updated Trabajador with ID: {} ", id);
        return trabajadorRepository.save(oldtrabajador);
    }

    public TrabajadorOutDto modifyDto(long id, TrabajadorDto trabajadorDto) {
        Trabajador trabajador = modelMapper.map(trabajadorDto, Trabajador.class);

        if (trabajadorDto.getServicioId() > 0) {
            Servicio servicio = servicioService.findById(trabajadorDto.getServicioId());
            validateServicioAsignable(servicio);
            trabajador.setServicios(servicio);
        } else {
            trabajador.setServicios(null);
        }

        if (trabajadorDto.getActividadId() > 0) {
            Actividad actividad = actividadService.findById(trabajadorDto.getActividadId());
            validateActividadAsignable(actividad);
            trabajador.setActividad(actividad);
        } else {
            trabajador.setActividad(null);
        }

        Trabajador updatedTrabajador = modify(id, trabajador);
        return toOutDto(updatedTrabajador);
    }

    @Transactional
    public AccessCodeResponseDto regenerateAccessCode(long id) {
        Trabajador trabajador = trabajadorRepository.findById(id)
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));

        AccessCredentialsDto credentials;
        if (trabajador.getUsuario() == null) {
            credentials = accessUserService.createAccessUser(
                    trabajador.getName() + " " + trabajador.getSurname(),
                    trabajador.getEmail(),
                    "TRABAJADOR"
            );
            trabajador.setUsuario(credentials.getUsuario());
            trabajadorRepository.save(trabajador);
        } else {
            credentials = accessUserService.regenerateAccessCode(trabajador.getUsuario());
        }

        return new AccessCodeResponseDto(
                credentials.getUsuario().getId(),
                credentials.getUsuario().getEmail(),
                credentials.getInitialPassword()
        );
    }

    @Transactional
    public void darDeBaja(long id, BajaRequestDto bajaRequest) {
        Trabajador trabajador = trabajadorRepository.findById(id).orElseThrow(()-> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));

        if (Boolean.FALSE.equals(trabajador.getActive())) {
            throw new BusinessRuleException("El trabajador con ID " + id + " ya fue dado de baja");
        }

        LocalDate outDate = bajaRequest.getOutDate() != null ? bajaRequest.getOutDate() : LocalDate.now();
        trabajador.setActive(false);
        trabajador.setOutDate(outDate);
        trabajador.setReason(bajaRequest.getReason().trim());
        trabajadorRepository.save(trabajador);
        accessUserService.deactivateAccessUser(trabajador.getUsuario());
        logger.info("Trabajador with ID: {} marked as inactive", id);
    }

    @Transactional
    public void reactivar(long id) {
        Trabajador trabajador = trabajadorRepository.findById(id)
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador con la ID:"+ id+ "no encontrado"));

        if (Boolean.TRUE.equals(trabajador.getActive())) {
            throw new BusinessRuleException("El trabajador con ID " + id + " ya esta activo");
        }

        trabajador.setActive(true);
        trabajador.setOutDate(null);
        trabajador.setReason(null);
        trabajadorRepository.save(trabajador);
        accessUserService.reactivateAccessUser(trabajador.getUsuario());
        logger.info("Trabajador with ID: {} reactivated", id);
    }

    private TrabajadorOutDto toOutDto(Trabajador trabajador) {
        TrabajadorOutDto dto = new TrabajadorOutDto();
        dto.setId(trabajador.getId());
        dto.setDni(trabajador.getDni());
        dto.setName(trabajador.getName());
        dto.setSurname(trabajador.getSurname());
        dto.setEmail(trabajador.getEmail());
        dto.setPhoneNumber(trabajador.getPhoneNumber());
        dto.setBirthDate(trabajador.getBirthDate());
        dto.setEntryDate(trabajador.getEntryDate());
        dto.setContractType(trabajador.getContractType());
        dto.setActive(trabajador.getActive());
        dto.setOutDate(trabajador.getOutDate());
        dto.setReason(trabajador.getReason());
        dto.setActividadOutDto(toActividadOutDto(trabajador.getActividad()));
        dto.setServicioOutDto(toServicioOutDto(trabajador.getServicios()));
        return dto;
    }

    private ActividadOutDto toActividadOutDto(Actividad actividad) {
        if (actividad == null || "ARCHIVED".equalsIgnoreCase(actividad.getStatus())) {
            return null;
        }

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

    private com.svalero.asociation.dto.ServicioOutDto toServicioOutDto(Servicio servicio) {
        if (servicio == null || "ARCHIVED".equalsIgnoreCase(servicio.getStatus())) {
            return null;
        }

        com.svalero.asociation.dto.ServicioOutDto dto = new com.svalero.asociation.dto.ServicioOutDto();
        dto.setId(servicio.getId());
        dto.setDescription(servicio.getDescription());
        dto.setPeriodicity(servicio.getPeriodicity());
        dto.setRequisites(servicio.getRequisites());
        dto.setDuration(servicio.getDuration());
        dto.setCapacity(servicio.getCapacity());
        dto.setStatus(servicio.getStatus());
        dto.setTrabajadoresIds(List.of());
        return dto;
    }

    private void validateServicioAsignable(Servicio servicio) {
        if (servicio != null && "ARCHIVED".equalsIgnoreCase(servicio.getStatus())) {
            throw new BusinessRuleException("No se puede asignar un trabajador a un servicio archivado");
        }
    }

    private void validateActividadAsignable(Actividad actividad) {
        if (actividad != null && "ARCHIVED".equalsIgnoreCase(actividad.getStatus())) {
            throw new BusinessRuleException("No se puede asignar un trabajador a una actividad archivada");
        }
    }
}
