package com.svalero.asociation.service;

import com.svalero.asociation.dto.LoginRequest;
import com.svalero.asociation.dto.LoginResponse;
import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.UsuarioRepository;
import com.svalero.asociation.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final JwtService jwtService = mock(JwtService.class);

    private final AuthService authService = new AuthService(
            authenticationManager,
            usuarioRepository,
            jwtService
    );

    @Test
    void loginReturnsTokenAndUserData() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@teagestion.local");
        loginRequest.setPassword("Admin1234");

        Rol rol = Rol.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        Usuario usuario = Usuario.builder()
                .id(1L)
                .name("Administrador")
                .email("admin@teagestion.local")
                .password("encoded-password")
                .active(true)
                .roles(Set.of(rol))
                .build();

        when(usuarioRepository.findByEmail("admin@teagestion.local"))
                .thenReturn(Optional.of(usuario));

        when(jwtService.generateToken(any()))
                .thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(1L, response.getId());
        assertEquals("Administrador", response.getName());
        assertEquals("admin@teagestion.local", response.getEmail());
        assertTrue(response.getRoles().contains("ADMIN"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail("admin@teagestion.local");
        verify(jwtService).generateToken(any());
    }
}
