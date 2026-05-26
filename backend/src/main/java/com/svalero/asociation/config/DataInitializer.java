package com.svalero.asociation.config;

import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.RolRepository;
import com.svalero.asociation.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RolRepository rolRepository,
                                      UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            List<String> roleNames = List.of(
                    "ADMIN",
                    "TRABAJADOR",
                    "ADMINISTRATIVA",
                    "VOLUNTARIO",
                    "SOCIO"
            );

            for (String roleName : roleNames) {
                rolRepository.findByName(roleName)
                        .orElseGet(() -> rolRepository.save(
                                Rol.builder()
                                        .name(roleName)
                                        .build()
                        ));
            }

            if (!usuarioRepository.existsByEmail("admin@teagestion.local")) {
                Rol adminRole = rolRepository.findByName("ADMIN")
                        .orElseThrow();

                Usuario admin = Usuario.builder()
                        .name("Administrador")
                        .email("admin@teagestion.local")
                        .password(passwordEncoder.encode("Admin1234"))
                        .active(true)
                        .roles(new HashSet<>(List.of(adminRole)))
                        .build();

                usuarioRepository.save(admin);
            }
        };
    }
}
