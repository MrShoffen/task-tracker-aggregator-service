package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskCommentsCountDto {
    private UUID taskId;
    private int commentsCount;
}
