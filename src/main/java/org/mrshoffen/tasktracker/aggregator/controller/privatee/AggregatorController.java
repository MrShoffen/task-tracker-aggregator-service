package org.mrshoffen.tasktracker.aggregator.controller.privatee;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceDto;
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
    public Mono<FullWorkspaceDto> getFullWorkspaceInformation(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                              @PathVariable("workspaceId") UUID workspaceId) {
        return aggregatorService.getFullWorkspaceInfo(userId, workspaceId);
    }
}