package com.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User registration payload")
public class RegisterRequest {

    @Schema(description = "User display name / username", example = "JohnDoe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Unique user email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password (minimum 6 characters)", example = "Password@123", minLength = 6, requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}