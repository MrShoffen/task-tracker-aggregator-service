package org.mrshoffen.tasktracker.aggregator.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.web.dto.StickerResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskCommentsCountDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class CommentsClient {

    private final WebClient taskWebClient;


    @TimeLimiter(name = "commentClient")
    @Retry(name = "commentClient")
    @CircuitBreaker(name = "commentClient", fallbackMethod = "getCommentsCountFallback")
    public Flux<TaskCommentsCountDto> getCommentsCount(UUID workspaceId) {
        return taskWebClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}/comments-count", workspaceId)
                .retrieve()
                .bodyToFlux(TaskCommentsCountDto.class);
    }

    public Flux<TaskCommentsCountDto> getCommentsCountFallback(UUID workspaceId, Throwable ex) {
        log.warn("Error while fetching comments for aggregating full workspace {} ", workspaceId, ex);
        return Flux.empty();
    }
}
