package com.svalero.asociation.service;

import com.svalero.asociation.dto.AccessCredentialsDto;
import com.svalero.asociation.exception.BusinessRuleException;
import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.RolRepository;
import com.svalero.asociation.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
public class AccessUserService {

    private static final String ACCESS_CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AccessUserService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccessCredentialsDto createAccessUser(String name, String email, String roleName) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new BusinessRuleException("Ya existe un usuario con el email " + email);
        }

        Rol role = rolRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessRuleException("El rol " + roleName + " no existe"));

        String initialPassword = generateAccessCode();

        Usuario usuario = Usuario.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(initialPassword))
                .active(true)
                .roles(new HashSet<>(Set.of(role)))
                .build();

        return new AccessCredentialsDto(usuarioRepository.save(usuario), initialPassword);
    }

    public AccessCredentialsDto regenerateAccessCode(Usuario usuario) {
        String initialPassword = generateAccessCode();
        usuario.setPassword(passwordEncoder.encode(initialPassword));
        usuario.setActive(true);

        return new AccessCredentialsDto(usuarioRepository.save(usuario), initialPassword);
    }

    public void deleteAccessUser(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            return;
        }

        usuarioRepository.delete(usuario);
    }

    private String generateAccessCode() {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                code.append('-');
            }

            int index = SECURE_RANDOM.nextInt(ACCESS_CODE_ALPHABET.length());
            code.append(ACCESS_CODE_ALPHABET.charAt(index));
        }

        return code.toString();
    }
}
