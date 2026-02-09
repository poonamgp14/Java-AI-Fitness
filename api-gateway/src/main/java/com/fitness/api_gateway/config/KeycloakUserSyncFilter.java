package com.fitness.api_gateway.config;


import com.fitness.api_gateway.user.RegisterRequest;
import com.fitness.api_gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String userPath = exchange.getRequest().getURI().getPath();
        UriTemplate uriTemplate = new UriTemplate("/users/{userId}");
        Map<String, String> parameters = uriTemplate.match(userPath);
        String userId = parameters.get("userId");

        RegisterRequest registerRequest = getUserDetailsFromToken(token);

        if (userId == null){
            userId = registerRequest.getKeycloakId();
        }

        if(userId != null && token != null){
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist ->{
                        if (!exist){
                            //REGISTER USER if doesn't exists
                            if (registerRequest != null){
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            }else {
                                return Mono.empty();
                            }
                        }else {
                            log.info("User with id {} already exists in the system. Skipping registration.", finalUserId);
                            return Mono.empty();
                        }
                    }).then(Mono.defer(()-> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }


        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetailsFromToken(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));
            registerRequest.setPassword("dummyPassword");
            registerRequest.setFirstName(claims.getStringClaim("given_name"));
            registerRequest.setLastName(claims.getStringClaim("family_name"));
            return registerRequest;
        }catch (Exception e){
            log.error("Error extracting user details from token: {}", e.getMessage());
            return null;
        }
    }
}
