package org.mrshoffen.tasktracker.aggregator.client;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class TaskClient {

    private final WebClient webClient;

    public Flux<TaskResponseDto> getTasks(UUID workspaceId) {
        return webClient
                .get()
                .uri("/aggregate-api/workspaces/{workspaceId}/tasks", workspaceId)
                .retrieve()
                .bodyToFlux(TaskResponseDto.class);


    }


}
