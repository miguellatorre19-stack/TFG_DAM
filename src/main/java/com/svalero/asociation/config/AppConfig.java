package com.svalero.asociation.config;

import com.svalero.asociation.dto.ActividadOutDto;
import com.svalero.asociation.dto.EstadoSocioDto;
import com.svalero.asociation.dto.ParticipanteDto;
import com.svalero.asociation.dto.ParticipanteOutDto;
import com.svalero.asociation.dto.ServicioOutDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.dto.TrabajadorOutDto;
import com.svalero.asociation.dto.UbicacionDto;
import com.svalero.asociation.dto.UbicacionOutDto;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.model.InscripcionActividad;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.model.Ubicacion;
import com.svalero.asociation.modelv2.ActividadDtoV2;
import com.svalero.asociation.modelv2.ActividadOutDtoV2;
import com.svalero.asociation.modelv2.SocioOutDtoV2;
import com.svalero.asociation.service.EstadoSocioService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(EstadoSocioService estadoSocioService){

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<Socio, Long> socioToId = context ->
                context.getSource() == null ? null : context.getSource().getId();

        Converter<List<InscripcionActividad>, List<ParticipanteDto>> inscripcionesToParticipantes = context ->
                context.getSource() == null ? List.of() :
                        context.getSource().stream()
                                .map(InscripcionActividad::getParticipante)
                                .filter(Objects::nonNull)
                                .map(participante -> modelMapper.map(participante, ParticipanteDto.class))
                                .toList();

        Converter<List<Trabajador>, List<Long>> trabajadoresToIds = context ->
                context.getSource() == null ? List.of() :
                        context.getSource().stream()
                                .filter(Objects::nonNull)
                                .map(Trabajador::getId)
                                .toList();

        Converter<Servicio, ServicioOutDto> servicioToServicioOutDto = context ->
                context.getSource() == null ? null : modelMapper.map(context.getSource(), ServicioOutDto.class);

        Converter<Socio, EstadoSocioDto> socioToEstadoSocioDto = context ->
                context.getSource() == null ? null :
                        estadoSocioService.calcularEstado(modelMapper.map(context.getSource(), SocioDto.class));

        Converter<UbicacionDto, Ubicacion> ubicacionDtoToUbicacion = context ->
                context.getSource() == null ? null : modelMapper.map(context.getSource(), Ubicacion.class);

        Converter<Ubicacion, UbicacionOutDto> ubicacionToUbicacionOutDto = context ->
                context.getSource() == null ? null : modelMapper.map(context.getSource(), UbicacionOutDto.class);

        modelMapper.typeMap(Participante.class, ParticipanteDto.class).addMappings(mapper -> {
            mapper.using(socioToId).map(Participante::getSocio, ParticipanteDto::setSocioID);
        });

        modelMapper.typeMap(Participante.class, ParticipanteOutDto.class).addMappings(mapper -> {
            mapper.using(socioToId).map(Participante::getSocio, ParticipanteOutDto::setSocioID);
        });

        modelMapper.typeMap(Socio.class, SocioDto.class).addMappings(mapper -> {
            mapper.map(Socio::getParticipanteList, SocioDto::setParticipanteDtoList);
        });

        modelMapper.typeMap(Socio.class, SocioOutDtoV2.class).addMappings(mapper -> {
            mapper.map(Socio::getParticipanteList, SocioOutDtoV2::setParticipanteDtoList);
            mapper.using(socioToEstadoSocioDto).map(src -> src, SocioOutDtoV2::setEstadoSocioDto);
        });

        modelMapper.typeMap(Actividad.class, ActividadOutDto.class).addMappings(mapper ->{
            mapper.using(inscripcionesToParticipantes).map(Actividad::getInscripciones, ActividadOutDto::setParticipanteDtoList);
        });

        modelMapper.typeMap(ActividadDtoV2.class, Actividad.class).addMappings(mapper -> {
            mapper.using(ubicacionDtoToUbicacion).map(ActividadDtoV2::getUbicacion, Actividad::setPlace);
        });

        modelMapper.typeMap(Actividad.class, ActividadOutDtoV2.class).addMappings(mapper -> {
            mapper.using(inscripcionesToParticipantes).map(Actividad::getInscripciones, ActividadOutDtoV2::setParticipanteDtoList);
            mapper.using(ubicacionToUbicacionOutDto).map(Actividad::getPlace, ActividadOutDtoV2::setUbicacionOutDto);
        });

        modelMapper.typeMap(Servicio.class, ServicioOutDto.class).addMappings(mapper -> {
            mapper.using(trabajadoresToIds).map(Servicio::getTrabajadoresAsignados, ServicioOutDto::setTrabajadoresIds);
        });

        modelMapper.typeMap(Trabajador.class, TrabajadorOutDto.class).addMappings(mapper -> {
            mapper.using(servicioToServicioOutDto).map(Trabajador::getServicios, TrabajadorOutDto::setServicioOutDto);
        });

        return  modelMapper;
    }
}
