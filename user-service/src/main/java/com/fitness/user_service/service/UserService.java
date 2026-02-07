package com.fitness.user_service.service;

import com.fitness.user_service.dto.RegisterRequest;
import com.fitness.user_service.dto.UserResponse;
import com.fitness.user_service.model.User;
import com.fitness.user_service.model.UserRole;
import com.fitness.user_service.repository.UserRepository;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public UserResponse registerUser(RegisterRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        User user = mapToUser(userRequest);
        User savedUser = this.userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    private User mapToUser(RegisterRequest userRequest) {
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        return user;
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }

    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return mapToUserResponse(user);

    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }
}
