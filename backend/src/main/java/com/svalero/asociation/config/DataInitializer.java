package com.svalero.asociation.config;

import com.svalero.asociation.model.Rol;
import com.svalero.asociation.model.Usuario;
import com.svalero.asociation.repository.RolRepository;
import com.svalero.asociation.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.svalero.asociation.model.Actividad;
import com.svalero.asociation.model.Participante;
import com.svalero.asociation.model.Servicio;
import com.svalero.asociation.model.Socio;
import com.svalero.asociation.model.Trabajador;
import com.svalero.asociation.repository.ActividadRepository;
import com.svalero.asociation.repository.ParticipanteRepository;
import com.svalero.asociation.repository.ServicioRepository;
import com.svalero.asociation.repository.SocioRepository;
import com.svalero.asociation.repository.TrabajadorRepository;

import java.time.LocalDate;

import java.util.HashSet;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "app.seed-data.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RolRepository rolRepository,
                                      UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder,
                                      SocioRepository socioRepository,
                                      ParticipanteRepository participanteRepository,
                                      ActividadRepository actividadRepository,
                                      ServicioRepository servicioRepository,
                                      TrabajadorRepository trabajadorRepository) {
        return args -> {
            List<String> roleNames = List.of(
                    "ADMIN",
                    "TRABAJADOR",
                    "ADMINISTRATIVA",
                    "VOLUNTARIO",
                    "PARTICIPANTE",
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

            if (socioRepository.count() == 0) {
                Socio socio1 = new Socio();
                socio1.setName("Laura");
                socio1.setSurname("Pérez");
                socio1.setDni("12345678A");
                socio1.setEmail("laura.perez@example.com");
                socio1.setPhoneNumber("600-123-456");
                socio1.setAddress("Calle Mayor 12, Zaragoza");
                socio1.setFamilyModel("Nuclear");
                socio1.setActive(true);
                socio1.setEntryDate(LocalDate.of(2026, 5, 27));

                Socio socio2 = new Socio();
                socio2.setName("Miguel");
                socio2.setSurname("Latorre");
                socio2.setDni("87654321B");
                socio2.setEmail("miguel.latorre@example.com");
                socio2.setPhoneNumber("611-222-333");
                socio2.setAddress("Avenida Goya 45, Zaragoza");
                socio2.setFamilyModel("Monoparental");
                socio2.setActive(true);
                socio2.setEntryDate(LocalDate.of(2026, 5, 27));

                socioRepository.saveAll(List.of(socio1, socio2));
            }

            if (participanteRepository.count() == 0) {
                List<Socio> socios = socioRepository.findAll();

                if (!socios.isEmpty()) {
                    Participante participante1 = new Participante();
                    participante1.setName("Daniel");
                    participante1.setSurname("Pérez");
                    participante1.setDni("11111111A");
                    participante1.setEmail("daniel.perez@example.com");
                    participante1.setPhoneNumber("622-111-222");
                    participante1.setBirthDate(LocalDate.of(2010, 4, 12));
                    participante1.setEntryDate(LocalDate.of(2026, 5, 27));
                    participante1.setNeeds("Apoyo en habilidades sociales");
                    participante1.setTypeRel("Hijo");
                    participante1.setActive(true);
                    participante1.setSocio(socios.get(0));

                    Participante participante2 = new Participante();
                    participante2.setName("Sofía");
                    participante2.setSurname("Latorre");
                    participante2.setDni("22222222B");
                    participante2.setEmail("sofia.latorre@example.com");
                    participante2.setPhoneNumber("633-222-333");
                    participante2.setBirthDate(LocalDate.of(2012, 9, 5));
                    participante2.setEntryDate(LocalDate.of(2026, 5, 27));
                    participante2.setNeeds("Apoyo en organización y comunicación");
                    participante2.setTypeRel("Hija");
                    participante2.setActive(true);
                    participante2.setSocio(socios.size() > 1 ? socios.get(1) : socios.get(0));

                    participanteRepository.saveAll(List.of(participante1, participante2));
                }
            }

            if (actividadRepository.count() == 0) {
                Actividad actividad1 = new Actividad();
                actividad1.setDescription("Taller de habilidades sociales");
                actividad1.setDayActivity(LocalDate.of(2026, 6, 10));
                actividad1.setTypeActivity("Taller");
                actividad1.setDuration(2.0f);
                actividad1.setCanJoin(true);
                actividad1.setCapacity(12);
                actividad1.setLatitude(41.6488);
                actividad1.setLongitude(-0.8891);

                Actividad actividad2 = new Actividad();
                actividad2.setDescription("Club de lectura inclusiva");
                actividad2.setDayActivity(LocalDate.of(2026, 6, 17));
                actividad2.setTypeActivity("Actividad grupal");
                actividad2.setDuration(1.5f);
                actividad2.setCanJoin(true);
                actividad2.setCapacity(10);
                actividad2.setLatitude(41.6488);
                actividad2.setLongitude(-0.8891);

                actividadRepository.saveAll(List.of(actividad1, actividad2));
            }

            if (servicioRepository.count() == 0) {
                Servicio servicio1 = new Servicio();
                servicio1.setDescription("Orientación familiar");
                servicio1.setPeriodicity("Mensual");
                servicio1.setRequisites("Ser socio activo");
                servicio1.setDuration(1.0f);
                servicio1.setCapacity(8);

                Servicio servicio2 = new Servicio();
                servicio2.setDescription("Apoyo psicológico individual");
                servicio2.setPeriodicity("Semanal");
                servicio2.setRequisites("Valoración previa por trabajador cualificado");
                servicio2.setDuration(1.0f);
                servicio2.setCapacity(6);

                servicioRepository.saveAll(List.of(servicio1, servicio2));
            }

            if (trabajadorRepository.count() == 0) {
                Trabajador trabajador1 = new Trabajador();
                trabajador1.setName("Elena");
                trabajador1.setSurname("Martín");
                trabajador1.setDni("33333333C");
                trabajador1.setEmail("elena.martin@example.com");
                trabajador1.setPhoneNumber("644-333-444");
                trabajador1.setBirthDate(LocalDate.of(1988, 3, 20));
                trabajador1.setEntryDate(LocalDate.of(2024, 9, 1));
                trabajador1.setContractType("Psicóloga");

                Trabajador trabajador2 = new Trabajador();
                trabajador2.setName("Javier");
                trabajador2.setSurname("Sanz");
                trabajador2.setDni("44444444D");
                trabajador2.setEmail("javier.sanz@example.com");
                trabajador2.setPhoneNumber("655-444-555");
                trabajador2.setBirthDate(LocalDate.of(1985, 7, 14));
                trabajador2.setEntryDate(LocalDate.of(2025, 1, 15));
                trabajador2.setContractType("Trabajador social");

                trabajadorRepository.saveAll(List.of(trabajador1, trabajador2));
            }

        };
    }
}
