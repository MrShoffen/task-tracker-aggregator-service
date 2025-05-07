package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;
import org.mrshoffen.tasktracker.commons.utils.link.Links;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class FullDeskResponse {

    private UUID id;
    private String name;
    private Instant createdAt;
    private UUID workspaceId;
    private Links api;
    private List<FullTaskResponse> tasks;
}
