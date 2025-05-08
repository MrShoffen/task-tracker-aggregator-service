package org.mrshoffen.tasktracker.aggregator.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.WorkspaceResponseDto;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

    public Mono<DeskResponseDto> getDeskInfo(UUID workspaceId, UUID deskId) {
        return webClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}/desks/{deskId}", workspaceId, deskId)
                .retrieve()
                .bodyToMono(DeskResponseDto.class);
    }

    public Mono<ResponseEntity<Void>> ensureUserOwnsDesk(UUID userId,
                                                         UUID workspaceId,
                                                         UUID deskId) {
        return webClient
                .get()
                .uri("/internal/workspaces/{userId}/{workspaceId}/desks/{deskId}", userId, workspaceId, deskId)
                .retrieve()
                .toBodilessEntity()
                .onErrorMap(WebClientResponseException.NotFound.class, e ->
                        new EntityNotFoundException("Отсутствует запрошенная доска или пространство")
                )
                .onErrorMap(WebClientResponseException.Forbidden.class, e ->
                        new AccessDeniedException("Пользователь не имеет доступа к данному пространству '%s'"
                                .formatted(workspaceId))
                );
    }


    public Flux<TaskResponseDto> getDesksFallback(UUID workspaceId, Throwable ex) {
        log.warn("Error while fetching tasks for aggregating {} ", workspaceId, ex);
        return Flux.empty();
    }


}
