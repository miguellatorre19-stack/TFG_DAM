package com.svalero.asociation.service;

import com.svalero.asociation.dto.LoginRequest;
import com.svalero.asociation.dto.LoginResponse;
import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.UsuarioRepository;
import com.svalero.asociation.security.JwtService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Profile("!test")
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getName)
                .collect(Collectors.toSet());

        User userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                roles.stream()
                        .map(role -> "ROLE_" + role)
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
        );

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                roles
        );
    }
}
