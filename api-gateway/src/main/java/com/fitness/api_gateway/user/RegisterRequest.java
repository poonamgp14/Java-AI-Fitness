package com.fitness.api_gateway.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email(message= "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    private String email;
    private String keycloakId;

    @Size(min= 6, message = "Password must be at least 6 characters long")
    @NotBlank(message = "Password is mandatory")
    private String password;
    private String firstName;
    private String lastName;
}
