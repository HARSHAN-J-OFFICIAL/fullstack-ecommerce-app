package com.ecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerAndLoginFlow_ShouldSucceed() throws Exception {
        String uniqueEmail = "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        // 1. Register API
        Map<String, String> registerMap = new HashMap<>();
        registerMap.put("username", "TestUser");
        registerMap.put("email", uniqueEmail);
        registerMap.put("password", "Test@12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerMap)))
                .andExpect(status().isCreated());

        // 2. Login API
        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("email", uniqueEmail);
        loginMap.put("password", "Test@12345");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMap)))
                .andExpect(status().isOk());
    }
}
