package org.mrshoffen.tasktracker.aggregator.dto;

import lombok.Data;
import org.mrshoffen.tasktracker.commons.utils.link.Links;
import org.mrshoffen.tasktracker.commons.web.dto.UserResponseDto;
import org.mrshoffen.tasktracker.commons.web.permissions.Permission;

import java.util.List;
import java.util.UUID;

@Data
public class FullUserPermissionResponse {

    UUID userId;
    UserResponseDto info;
    List<Permission> permissions;

    private Links api;
}
