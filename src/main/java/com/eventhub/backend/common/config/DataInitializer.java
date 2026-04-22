package com.eventhub.backend.common.config;

import com.eventhub.backend.auth.entity.*;
import com.eventhub.backend.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByEmail("admin@test.com").isEmpty()) {

                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@test.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);

                repo.save(admin);
            }
        };
    }
}