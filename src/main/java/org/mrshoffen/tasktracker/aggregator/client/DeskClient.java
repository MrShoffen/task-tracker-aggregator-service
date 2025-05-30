package org.mrshoffen.tasktracker.aggregator.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class DeskClient {

    private final WebClient webClient;

    @TimeLimiter(name = "deskClient")
    @Retry(name = "deskClient")
    @CircuitBreaker(name = "deskClient", fallbackMethod = "getDesksFallback")
    public Flux<DeskResponseDto> getDesks(UUID workspaceId) {
        return webClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}/desks", workspaceId)
                .retrieve()
                .bodyToFlux(DeskResponseDto.class);
    }

    public Flux<TaskResponseDto> getDesksFallback(UUID workspaceId, Throwable ex) {
        log.warn("Error while fetching tasks for aggregating {} ", workspaceId, ex);
        return Flux.empty();
    }


}
