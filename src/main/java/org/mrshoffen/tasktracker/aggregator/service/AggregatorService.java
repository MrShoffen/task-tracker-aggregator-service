package org.mrshoffen.tasktracker.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.aggregator.client.*;
import org.mrshoffen.tasktracker.aggregator.dto.FullDeskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullTaskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullUserPermissionResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceResponse;
import org.mrshoffen.tasktracker.aggregator.mapper.AggregatorMapper;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.StickerResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskCommentsCountDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.WorkspaceResponseDto;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    private final PermissionsClient permissionsClient;

    private final StickerClient stickerClient;

    private final CommentsClient commentsClient;

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

        Mono<List<StickerResponseDto>> stickerMono = stickerClient
                .getStickersInWorkspace(workspaceId)
                .collectList();

        Mono<List<TaskCommentsCountDto>> commentCountMono = commentsClient
                .getCommentsCount(workspaceId)
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


        return Mono.zip(workspaceMono, deskMono, taskMono, permissions, stickerMono, commentCountMono)
                .map(tuple -> assembleFullWorkspaceDto(tuple.getT1(), tuple.getT2(),
                        tuple.getT3(), tuple.getT4(),
                        tuple.getT5(), tuple.getT6()));
    }


    private FullWorkspaceResponse assembleFullWorkspaceDto(WorkspaceResponseDto workspace,
                                                           List<DeskResponseDto> desks,
                                                           List<TaskResponseDto> tasks,
                                                           List<FullUserPermissionResponse> permissions,
                                                           List<StickerResponseDto> stickers,
                                                           List<TaskCommentsCountDto> commentsCount) {

        Map<UUID, Long> commentsCountMap = commentsCount.stream()
                .collect(Collectors.toMap(TaskCommentsCountDto::getTaskId, TaskCommentsCountDto::getCount));

        List<FullDeskResponse> fullDesksResponses = desks.stream()
                .map(desk -> assembleFullDeskResponse(desk, tasks, stickers, commentsCountMap))
                .toList();

        FullWorkspaceResponse fullWorkspaceResponse = aggregatorMapper.toFullResponse(workspace);
        fullWorkspaceResponse.setDesks(fullDesksResponses);
        fullWorkspaceResponse.setUsersAndPermissions(permissions);

        return fullWorkspaceResponse;
    }


    private FullDeskResponse assembleFullDeskResponse(DeskResponseDto desk,
                                                      List<TaskResponseDto> tasks,
                                                      List<StickerResponseDto> stickers,
                                                      Map<UUID, Long> commentsCountMap) {
        List<FullTaskResponse> fullTasks = tasks.stream()
                .filter(task -> task.getDeskId().equals(desk.getId()))
                .map(task -> assembleFullTaskResponse(task, stickers, commentsCountMap))
                .toList();

        return aggregatorMapper.toFullResponse(desk, fullTasks);
    }

    private FullTaskResponse assembleFullTaskResponse(TaskResponseDto task,
                                                      List<StickerResponseDto> stickers,
                                                      Map<UUID, Long> commentsCountMap
                                                      ) {
        List<StickerResponseDto> stickersOnTask = stickers.stream()
                .filter(sticker -> sticker.getTaskId().equals(task.getId()))
                .toList();
        return aggregatorMapper.toFullResponse(task, stickersOnTask, commentsCountMap.get(task.getId()));
    }

    public Mono<FullWorkspaceResponse> getFullPublicWorkspaceInfo(UUID workspaceId) {
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

        return workspaceMono
                .flatMap(ws -> {
                    if (!ws.getIsPublic()) {
                        return Mono.error(new AccessDeniedException("Данное пространство не является публичным"));
                    } else {
                        return Mono.zip(workspaceMono, deskMono, taskMono, permissions);
                    }
                })
                .map(tuple -> assembleFullWorkspaceDto(tuple.getT1(), tuple.getT2(), tuple.getT3(), null, null, null));
    }
}
