package org.mrshoffen.tasktracker.aggregator.controller.protectedd;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.aggregator.dto.FullDeskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceResponse;
import org.mrshoffen.tasktracker.aggregator.service.AggregatorService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mrshoffen.tasktracker.commons.web.authentication.AuthenticationAttributes.AUTHORIZED_USER_HEADER_NAME;

/**
 * Для получения данных приватных workspace
 * (доступны только создавшим их пользователям)
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces")
public class AggregatorController {


    private final AggregatorService aggregatorService;

    @GetMapping("/{workspaceId}/full")
    public Mono<FullWorkspaceResponse> getFullWorkspaceInformation(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                                   @PathVariable("workspaceId") UUID workspaceId) {
        return aggregatorService.getFullWorkspaceInfo(userId, workspaceId);
    }

    @GetMapping("/{workspaceId}/desks/{deskId}/full")
    public Mono<FullDeskResponse> getFullDeskInformation(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                         @PathVariable("workspaceId") UUID workspaceId,
                                                         @PathVariable("deskId") UUID deskId) {
        return aggregatorService.getFullDeskInfo(userId, workspaceId, deskId);

    }
}