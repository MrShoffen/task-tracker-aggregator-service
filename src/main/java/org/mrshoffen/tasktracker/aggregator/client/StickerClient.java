package org.mrshoffen.tasktracker.aggregator.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.web.dto.StickerResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class StickerClient {

    private final WebClient taskWebClient;


    @TimeLimiter(name = "stickerClient")
    @Retry(name = "stickerClient")
    @CircuitBreaker(name = "stickerClient", fallbackMethod = "getStickersFallback")
    public Flux<StickerResponseDto> getStickersInWorkspace(UUID workspaceId) {
        return taskWebClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}/stickers", workspaceId)
                .retrieve()
                .bodyToFlux(StickerResponseDto.class);
    }

    public Flux<StickerResponseDto> getStickersFallback(UUID workspaceId, Throwable ex) {
        log.warn("Error while fetching stickers for aggregating full workspace {} ", workspaceId, ex);
        return Flux.empty();
    }
}
