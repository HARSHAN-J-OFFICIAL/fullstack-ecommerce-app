package com.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User login credentials payload")
public class LoginRequest {

    @Schema(description = "Registered email address", example = "admin@ecommerce.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Account password", example = "Admin@123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    private String password;
}