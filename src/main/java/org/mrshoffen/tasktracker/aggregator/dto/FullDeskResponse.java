package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;
import org.mrshoffen.tasktracker.commons.utils.link.Links;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class FullDeskResponse {

    private UUID id;
    private String name;
    private Instant createdAt;
    private String color;
    private UUID workspaceId;
    private Long orderIndex;
    private Links api;
    private List<FullTaskResponse> tasks;
}
