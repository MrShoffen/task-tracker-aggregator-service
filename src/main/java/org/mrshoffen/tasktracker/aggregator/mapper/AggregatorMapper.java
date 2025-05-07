package org.mrshoffen.tasktracker.aggregator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mrshoffen.tasktracker.aggregator.dto.FullDeskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullTaskResponse;
import org.mrshoffen.tasktracker.aggregator.dto.FullWorkspaceDto;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.WorkspaceResponseDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AggregatorMapper {

    FullTaskResponse toFullResponse(TaskResponseDto taskResponseDto);

    FullDeskResponse toFullResponse(DeskResponseDto deskResponseDto);

    FullWorkspaceDto toFullResponse(WorkspaceResponseDto workspaceResponseDto);
}
