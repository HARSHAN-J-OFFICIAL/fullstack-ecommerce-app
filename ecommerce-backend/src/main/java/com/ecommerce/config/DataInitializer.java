package com.ecommerce.config;

import com.ecommerce.entity.Role;
import com.ecommerce.enums.RoleName;
import com.ecommerce.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        if (roleRepository.findByName(
                RoleName.ROLE_USER).isEmpty()) {

            roleRepository.save(
                    Role.builder()
                            .name(RoleName.ROLE_USER)
                            .build()
            );
        }

        if (roleRepository.findByName(
                RoleName.ROLE_ADMIN).isEmpty()) {

            roleRepository.save(
                    Role.builder()
                            .name(RoleName.ROLE_ADMIN)
                            .build()
            );
        }
    }
}