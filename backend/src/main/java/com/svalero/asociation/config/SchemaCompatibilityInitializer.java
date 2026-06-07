package com.svalero.asociation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("!test")
public class SchemaCompatibilityInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SchemaCompatibilityInitializer.class);

    @Bean
    CommandLineRunner ensureOptionalParticipantPhoneNumber(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                String isNullable = jdbcTemplate.queryForObject(
                        """
                        SELECT IS_NULLABLE
                        FROM INFORMATION_SCHEMA.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = 'participantes'
                          AND COLUMN_NAME = 'phone_number'
                        """,
                        String.class
                );

                if ("NO".equalsIgnoreCase(isNullable)) {
                    jdbcTemplate.execute("ALTER TABLE participantes MODIFY phone_number VARCHAR(255) NULL");
                    logger.info("Schema compatibility fix applied: participantes.phone_number is now nullable");
                }
            } catch (Exception ex) {
                logger.warn("Schema compatibility check for participantes.phone_number could not be completed", ex);
            }
        };
    }
}
