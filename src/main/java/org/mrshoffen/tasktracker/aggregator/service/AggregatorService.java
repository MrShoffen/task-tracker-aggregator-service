package org.mrshoffen.tasktracker.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.aggregator.client.DeskClient;
import org.mrshoffen.tasktracker.aggregator.client.TaskClient;
import org.mrshoffen.tasktracker.aggregator.client.WorkspaceClient;
import org.mrshoffen.tasktracker.aggregator.dto.FullDeskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullTaskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceResponse;
import org.mrshoffen.tasktracker.aggregator.mapper.AggregatorMapper;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.WorkspaceResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AggregatorService {

    private final WorkspaceClient workspaceClient;

    private final DeskClient deskClient;

    private final TaskClient taskClient;

    private final AggregatorMapper aggregatorMapper;

    public Mono<FullWorkspaceResponse> getFullWorkspaceInfo(UUID userId, UUID workspaceId) {
        Mono<WorkspaceResponseDto> workspaceMono = workspaceClient
                .getWorkspaceInfo(workspaceId);

        Mono<List<DeskResponseDto>> deskMono = deskClient
                .getDesks(workspaceId)
                .collectList();

        Mono<List<TaskResponseDto>> taskMono = taskClient
                .getTasksInWorkspace(workspaceId)
                .collectList();

        return workspaceClient
                .ensureUserOwnsWorkspace(userId, workspaceId)
                .then(Mono.zip(workspaceMono, deskMono, taskMono))
                .map(tuple -> assembleFullWorkspaceDto(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    public Mono<FullDeskResponse> getFullDeskInfo(UUID userId, UUID workspaceId, UUID deskId) {
        Mono<DeskResponseDto> deskMono = deskClient
                .getDeskInfo(workspaceId, deskId);

        Mono<List<TaskResponseDto>> taskMono = taskClient
                .getTasksInDesk(workspaceId, deskId)
                .collectList();

        return deskClient
                .ensureUserOwnsDesk(userId, workspaceId, deskId)
                .then(Mono.zip(deskMono, taskMono))
                .map(tuple ->
                        assembleFullDeskResponse(tuple.getT1(), tuple.getT2())
                );
    }


    private FullWorkspaceResponse assembleFullWorkspaceDto(WorkspaceResponseDto workspace, List<DeskResponseDto> desks, List<TaskResponseDto> tasks) {
        List<FullDeskResponse> fullDesksResponses = desks.stream()
                .map(desk -> assembleFullDeskResponse(desk, tasks))
                .toList();

        FullWorkspaceResponse fullWorkspaceResponse = aggregatorMapper.toFullResponse(workspace);
        fullWorkspaceResponse.setDesks(fullDesksResponses);

        return fullWorkspaceResponse;
    }

    private FullDeskResponse assembleFullDeskResponse(DeskResponseDto desk, List<TaskResponseDto> tasks) {
        FullDeskResponse fullDeskResponse = aggregatorMapper.toFullResponse(desk);

        List<FullTaskResponse> fullTasks = tasks.stream()
                .map(aggregatorMapper::toFullResponse)
                .filter(fullTask -> fullTask.getDeskId().equals(desk.getId()))
                .toList();

        fullDeskResponse.setTasks(fullTasks);
        return fullDeskResponse;
    }
}
