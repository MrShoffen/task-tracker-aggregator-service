package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;
import org.mrshoffen.tasktracker.commons.utils.link.Links;

import java.time.Instant;
import java.util.UUID;

@Data
public class FullTaskResponse {
    private UUID id;
    private String name;
    private Boolean completed;
    private Instant createdAt;
    private UUID parentTaskId;
    private UUID workspaceId;
    private UUID deskId;
    private Long orderIndex;
    private Links api;
}
