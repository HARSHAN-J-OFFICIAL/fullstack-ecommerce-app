package com.ecommerce.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin@ecommerce.com", roles = {"USER"})
    void postOrder_WhenCartIsEmpty_ShouldReturnError() throws Exception {
        // Performing order placement on empty cart expects client error (400 Bad Request or 404 / 500)
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status < 400) {
                        throw new AssertionError("Expected client/server error when placing empty cart order, but got " + status);
                    }
                });
    }
}
