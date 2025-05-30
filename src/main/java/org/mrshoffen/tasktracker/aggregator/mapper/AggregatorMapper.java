package org.mrshoffen.tasktracker.aggregator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mrshoffen.tasktracker.aggregator.dto.FullDeskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullTaskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullUserPermissionResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceResponse;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.StickerResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskCommentsCountDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.UserPermissionResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.WorkspaceResponseDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AggregatorMapper {

    FullTaskResponse toFullResponse(TaskResponseDto taskResponseDto, List<StickerResponseDto> stickers, Long commentsCount);

    FullDeskResponse toFullResponse(DeskResponseDto deskResponseDto, List<FullTaskResponse> tasks);

    FullWorkspaceResponse toFullResponse(WorkspaceResponseDto workspaceResponseDto);

    FullUserPermissionResponse toFullResponse(UserPermissionResponseDto userPermissionResponseDto);
}
