package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;
import org.mrshoffen.tasktracker.commons.web.dto.StickerResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskCommentsCountDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class FullTaskResponse {
    private UUID id;
    private String name;
    private Boolean completed;
    private Instant createdAt;
    private String color;
    private String coverUrl;
    private UUID userId;
    private UUID workspaceId;
    private UUID deskId;
    private Long orderIndex;
    private Long commentsCount;
    private List<StickerResponseDto> stickers;
}
