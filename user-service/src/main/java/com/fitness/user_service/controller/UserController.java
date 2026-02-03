package com.fitness.user_service.controller;

import com.fitness.user_service.dto.RegisterRequest;
import com.fitness.user_service.dto.UserResponse;
import com.fitness.user_service.model.User;
import com.fitness.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userService.getUserProfile(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest userRequest) {
        UserResponse savedUser =  this.userService.registerUser(userRequest);
        System.out.println("---UserController----");
        System.out.println(savedUser.getRole());
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUserExists(@PathVariable Long userId) {
        return userService.existsById(userId)
                ? ResponseEntity.ok(true)
                : ResponseEntity.ok(false);
    }
}
