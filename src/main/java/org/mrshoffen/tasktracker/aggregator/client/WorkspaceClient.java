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

}
