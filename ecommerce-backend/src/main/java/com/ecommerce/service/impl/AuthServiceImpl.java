package com.ecommerce.service.impl;

import com.ecommerce.dto.JwtResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;

import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;

import com.ecommerce.enums.RoleName;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;

import com.ecommerce.security.CustomUserDetailsService;
import com.ecommerce.security.JwtUtil;

import com.ecommerce.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new ResourceNotFoundException("Email already exists");
        }

        Role userRole = roleRepository.findByName(
                RoleName.ROLE_USER
        ).orElseThrow(() ->
                new ResourceNotFoundException("ROLE_USER not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
    }

    @Override
    public JwtResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        request.getEmail()
                );

        String token = jwtUtil.generateToken(userDetails);

        return new JwtResponse(token);
    }
}