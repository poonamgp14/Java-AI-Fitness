package com.fitness.api_gateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userID){
        return userServiceWebClient.get()
                .uri("/users/{userID}/validate", userID)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                        return Mono.error(new RuntimeException("User Service is currently unavailable. Please try again later."));
                    }
                    else if (e.getStatusCode() == HttpStatus.BAD_REQUEST){
                        return Mono.error(new RuntimeException("BAD_REQUEST while validating the user: " + e.getMessage()));
                    }
                    else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
                        return Mono.error(new RuntimeException("INTERNAL_SERVER_ERROR while validating the user: " + e.getMessage()));
                    }
                    return Mono.error(new RuntimeException("An error occurred while validating the user: " + e.getMessage()));
                });

    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        return userServiceWebClient.post()
                .uri("/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                        return Mono.error(new RuntimeException("User Service is currently unavailable. Please try again later."));
                    }
                    else if (e.getStatusCode() == HttpStatus.BAD_REQUEST){
                        return Mono.error(new RuntimeException("BAD_REQUEST while registering the user: " + e.getMessage()));
                    }
                    else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
                        return Mono.error(new RuntimeException("INTERNAL_SERVER_ERROR while registering the user: " + e.getMessage()));
                    }
                    return Mono.error(new RuntimeException("An error occurred while registering the user: " + e.getMessage()));
                });
    }
}
