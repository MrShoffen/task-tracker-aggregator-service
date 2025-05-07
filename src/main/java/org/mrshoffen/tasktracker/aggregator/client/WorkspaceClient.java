package org.mrshoffen.tasktracker.aggregator.client;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.WorkspaceResponseDto;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class WorkspaceClient {

    private final WebClient webClient;

    public Mono<WorkspaceResponseDto> getWorkspaceInfo(UUID workspaceId) {
        return webClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}", workspaceId)
                .retrieve()
                .bodyToMono(WorkspaceResponseDto.class);
    }

    public Mono<ResponseEntity<Void>> ensureUserOwnsWorkspace(UUID userId, UUID workspaceId) {
        return webClient
                .get()
                .uri("/internal/workspaces/{userId}/{workspaceId}", userId, workspaceId)
                .retrieve()
                .toBodilessEntity()
                .onErrorMap(WebClientResponseException.NotFound.class, e ->
                        new EntityNotFoundException("Пространство с id '%s' не существует"
                                .formatted(workspaceId))
                )
                .onErrorMap(WebClientResponseException.Forbidden.class, e ->
                        new AccessDeniedException("Пользователь не имеет доступа к данному пространству '%s'"
                                .formatted(workspaceId))
                );
    }

}
