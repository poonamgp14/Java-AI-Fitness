package com.fitness.api_gateway.user;

import com.fitness.api_gateway.user.UserRole;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserResponse {
    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
