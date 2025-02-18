package com.example.minglethedog;

import com.example.minglethedog.dto.CustomUserDetails;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class MinglethedogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinglethedogApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return Optional.of(((CustomUserDetails) principal).getUsername());
            }
            return Optional.empty();
        };
    }

}
