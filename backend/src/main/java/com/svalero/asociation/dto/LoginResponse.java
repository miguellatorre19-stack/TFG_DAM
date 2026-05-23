package com.svalero.asociation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type;
    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
}
