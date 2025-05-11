package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;
import org.mrshoffen.tasktracker.commons.utils.link.Links;
import org.mrshoffen.tasktracker.commons.web.dto.UserPermissionResponseDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class FullWorkspaceResponse {
    private UUID id;
    private String name;
    private Instant createdAt;
    private Links api;
    private List<FullDeskResponse> desks;

    private List<FullUserPermissionResponse> usersAndPermissions;
}
