package org.mrshoffen.tasktracker.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.aggregator.client.DeskClient;
import org.mrshoffen.tasktracker.aggregator.client.PermissionsClient;
import org.mrshoffen.tasktracker.aggregator.client.TaskClient;
import org.mrshoffen.tasktracker.aggregator.client.UserClient;
import org.mrshoffen.tasktracker.aggregator.client.WorkspaceClient;
import org.mrshoffen.tasktracker.aggregator.dto.FullDeskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullTaskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullUserPermissionResponse;
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

    private final PermissionsClient permissionsClient;

    private final AggregatorMapper aggregatorMapper;
    private final UserClient userClient;

    public Mono<FullWorkspaceResponse> getFullWorkspaceInfo(UUID userId, UUID workspaceId) {
        Mono<WorkspaceResponseDto> workspaceMono = workspaceClient
                .getWorkspaceInfo(workspaceId);

        Mono<List<DeskResponseDto>> deskMono = deskClient
                .getDesks(workspaceId)
                .collectList();

        Mono<List<TaskResponseDto>> taskMono = taskClient
                .getTasksInWorkspace(workspaceId)
                .collectList();

        Mono<List<FullUserPermissionResponse>> permissions = permissionsClient
                .getAllPermissionsInWorkspace(workspaceId)
                .map(aggregatorMapper::toFullResponse)
                .flatMap(perm ->
                        userClient.getUserInformation(perm.getUserId())
                                .map(userInfo -> {
                                    perm.setInfo(userInfo);
                                    return perm;
                                })
                )
                .collectList();


        return Mono.zip(workspaceMono, deskMono, taskMono, permissions)
                .map(tuple -> assembleFullWorkspaceDto(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()));
    }

    public Mono<FullDeskResponse> getFullDeskInfo(UUID userId, UUID workspaceId, UUID deskId) {
        Mono<DeskResponseDto> deskMono = deskClient
                .getDeskInfo(workspaceId, deskId);

        Mono<List<TaskResponseDto>> taskMono = taskClient
                .getTasksInDesk(workspaceId, deskId)
                .collectList();

        return Mono.zip(deskMono, taskMono)
                .map(tuple ->
                        assembleFullDeskResponse(tuple.getT1(), tuple.getT2())
                );
    }

    private FullWorkspaceResponse assembleFullWorkspaceDto(WorkspaceResponseDto workspace, List<DeskResponseDto> desks,
                                                           List<TaskResponseDto> tasks, List<FullUserPermissionResponse> permissions) {
        List<FullDeskResponse> fullDesksResponses = desks.stream()
                .map(desk -> assembleFullDeskResponse(desk, tasks))
                .toList();

        FullWorkspaceResponse fullWorkspaceResponse = aggregatorMapper.toFullResponse(workspace);
        fullWorkspaceResponse.setDesks(fullDesksResponses);
        fullWorkspaceResponse.setUsersAndPermissions(permissions);

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
