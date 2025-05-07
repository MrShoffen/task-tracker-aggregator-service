package org.mrshoffen.tasktracker.aggregator.client;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class DeskClient {

    private final WebClient webClient;

    public Flux<DeskResponseDto> getDesks(UUID workspaceId) {
        return webClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}/desks", workspaceId)
                .retrieve()
                .bodyToFlux(DeskResponseDto.class);
    }


}
