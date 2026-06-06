package com.svalero.asociation.dto;

import com.svalero.asociation.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessCredentialsDto {
    private Usuario usuario;
    private String initialPassword;
}
