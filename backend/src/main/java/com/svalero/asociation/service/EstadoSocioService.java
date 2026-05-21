package com.svalero.asociation.service;

import com.svalero.asociation.dto.EstadoSocioDto;
import com.svalero.asociation.dto.SocioDto;
import com.svalero.asociation.model.Socio;
import org.springframework.stereotype.Service;

@Service
public class EstadoSocioService {

    public EstadoSocioDto calcularEstado( SocioDto socioDto){
        boolean activo = true;
        boolean cuotasAlDia = true;
        double importePendiente = 0.0;
        boolean documentacionCompleta = true;

        EstadoSocioDto estado = new EstadoSocioDto();
        estado.setActivo(activo);
        estado.setCuotasAlDia(cuotasAlDia);
        estado.setImportePendiente(importePendiente);

        return estado;
    }

}
