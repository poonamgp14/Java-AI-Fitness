package com.fitness.activity_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validateUser(Long userID){
        try{
            return userServiceWebClient.get()
                    .uri("/users/{userID}/validate", userID)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }catch(WebClientResponseException e){
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new RuntimeException("User not found with id: " + userID);
            else if(e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                throw new RuntimeException("User Service is currently unavailable. Please try again later.");
            else
                throw new RuntimeException("An error occurred while validating user with id: " + userID);
        }
    }
}
