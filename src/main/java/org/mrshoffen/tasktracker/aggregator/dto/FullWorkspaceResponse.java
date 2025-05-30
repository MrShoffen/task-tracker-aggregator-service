package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class FullWorkspaceResponse {
    private UUID id;
    private String name;
    private Instant createdAt;
    private String coverUrl;
    private Boolean isPublic;
    private UUID userId;
    private List<FullDeskResponse> desks;

    private List<FullUserPermissionResponse> usersAndPermissions;
}
