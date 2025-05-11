package org.mrshoffen.tasktracker.aggregator.client;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class UserClient {

    private final WebClient webClient;

    @TimeLimiter(name = "userClient")
    @Retry(name = "userClient")
    @CircuitBreaker(name = "userClient", fallbackMethod = "getUserFallback")
    public Mono<String> getUserEmail(UUID userId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/users/email")
                        .queryParam("id", userId)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    Mono<String> getUserFallback(UUID userId, Throwable ex) {
        log.warn("Error while fetching user mail {} ", userId, ex);
        return Mono.just("User - " + userId);
    }
}
