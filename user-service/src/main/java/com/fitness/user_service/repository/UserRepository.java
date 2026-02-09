package com.fitness.user_service.repository;

import com.fitness.user_service.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByKeycloakId(String userId);

    User findByEmail(@Email(message= "Invalid email format") @NotBlank(message = "Email is mandatory") String email);
}
