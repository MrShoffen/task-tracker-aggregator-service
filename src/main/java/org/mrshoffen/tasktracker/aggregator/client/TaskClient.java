package org.mrshoffen.tasktracker.aggregator.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class TaskClient {

    private final WebClient taskWebClient;


    @TimeLimiter(name = "taskClient")
    @Retry(name = "taskClient")
    @CircuitBreaker(name = "taskClient", fallbackMethod = "getTasksFallback")
    public Flux<TaskResponseDto> getTasks(UUID workspaceId) {
        return taskWebClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}/tasks", workspaceId)
                .retrieve()
                .bodyToFlux(TaskResponseDto.class);


    }

    public Flux<TaskResponseDto> getTasksFallback(UUID workspaceId, Throwable ex) {
        log.warn("Error while fetching tasks for aggregating {} ", workspaceId, ex);
        return Flux.empty();
    }

}
