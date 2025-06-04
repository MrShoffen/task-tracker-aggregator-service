package org.mrshoffen.tasktracker.aggregator.controller;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceResponse;
import org.mrshoffen.tasktracker.aggregator.service.AggregatorService;
import org.mrshoffen.tasktracker.aggregator.service.PermissionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mrshoffen.tasktracker.commons.web.authentication.AuthenticationAttributes.AUTHORIZED_USER_HEADER_NAME;
import static org.mrshoffen.tasktracker.commons.web.permissions.Permission.READ_WORKSPACE_CONTENT;

/**
 * Для получения данных приватных workspace
 * (доступны только создавшим их пользователям)
 **/
@RestController
@RequiredArgsConstructor
public class AggregatorController {

    private final PermissionsService permissionsService;

    private final AggregatorService aggregatorService;

    @GetMapping("/workspaces/{workspaceId}/full")
    public Mono<FullWorkspaceResponse> getFullWorkspaceInformation(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                                   @PathVariable("workspaceId") UUID workspaceId) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, READ_WORKSPACE_CONTENT)
                .then(aggregatorService
                        .getFullWorkspaceInfo(userId, workspaceId)
                );
    }

    @GetMapping("/public-workspaces/{workspaceId}/full")
    public Mono<FullWorkspaceResponse> getFullPublicWorkspaceInformation(@PathVariable("workspaceId") UUID workspaceId) {
        return aggregatorService
                .getFullPublicWorkspaceInfo(workspaceId);

    }
}