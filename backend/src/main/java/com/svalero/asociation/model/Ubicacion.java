package com.svalero.asociation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Default;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Ubicacion {
    private String place_direction = "";
    private Double latitud = 0.0;
    private Double longitud = 0.0;
}
