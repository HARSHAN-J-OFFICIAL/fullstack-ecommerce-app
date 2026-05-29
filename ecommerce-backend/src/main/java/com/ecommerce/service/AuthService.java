package com.ecommerce.service;

import com.ecommerce.dto.JwtResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    JwtResponse login(LoginRequest request);
}