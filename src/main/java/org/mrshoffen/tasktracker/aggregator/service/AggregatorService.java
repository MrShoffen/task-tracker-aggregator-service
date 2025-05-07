package org.mrshoffen.tasktracker.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.aggregator.client.DeskClient;
import org.mrshoffen.tasktracker.aggregator.client.TaskClient;
import org.mrshoffen.tasktracker.aggregator.client.WorkspaceClient;
import org.mrshoffen.tasktracker.aggregator.dto.FullDeskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullTaskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceDto;
import org.mrshoffen.tasktracker.aggregator.mapper.AggregatorMapper;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.WorkspaceResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AggregatorService {

    private final WorkspaceClient workspaceClient;

    private final DeskClient deskClient;

    private final TaskClient taskClient;

    private final AggregatorMapper aggregatorMapper;

    public Mono<FullWorkspaceDto> getFullWorkspaceInfo(UUID userId, UUID workspaceId) {
        Mono<WorkspaceResponseDto> workspaceMono = workspaceClient
                .getWorkspaceInfo(workspaceId);

        Mono<List<DeskResponseDto>> deskMono = deskClient
                .getDesks(workspaceId)
                .collectList();

        Mono<List<TaskResponseDto>> taskMono = taskClient
                .getTasks(workspaceId)
                .collectList();

        return workspaceClient
                .ensureUserOwnsWorkspace(userId, workspaceId)
                .then(Mono.zip(workspaceMono, deskMono, taskMono))
                .map(this::aggregateTupleToWorkspaceDto);

    }

    private FullWorkspaceDto aggregateTupleToWorkspaceDto(Tuple3<WorkspaceResponseDto, List<DeskResponseDto>, List<TaskResponseDto>> objects) {
        WorkspaceResponseDto workspace = objects.getT1();
        List<DeskResponseDto> desks = objects.getT2();
        List<TaskResponseDto> tasks = objects.getT3();

        List<FullDeskResponse> fullDesksResponse = aggregateTasksToDesks(desks, tasks);

        FullWorkspaceDto fullResponse = aggregatorMapper.toFullResponse(workspace);
        fullResponse.setDesks(fullDesksResponse);

        return fullResponse;
    }

    private List<FullDeskResponse> aggregateTasksToDesks(List<DeskResponseDto> desks, List<TaskResponseDto> tasks) {
        Map<UUID, List<FullTaskResponse>> groupedTasks = tasks.stream()
                .map(aggregatorMapper::toFullResponse)
                .collect(Collectors.groupingBy(FullTaskResponse::getDeskId));

        List<FullDeskResponse> list = desks.stream()
                .map(aggregatorMapper::toFullResponse)
                .toList();

        for (FullDeskResponse fullDeskResponse : list) {
            if (groupedTasks.get(fullDeskResponse.getId()) != null) {
                fullDeskResponse.setTasks(groupedTasks.get(fullDeskResponse.getId()));
            } else {
                fullDeskResponse.setTasks(Collections.emptyList());
            }
        }

        return list;
    }

}
