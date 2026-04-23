package com.manutrack.module.iam.mapper;

import com.manutrack.module.iam.dto.IamDtos;
import com.manutrack.module.iam.entity.AuditLog;
import com.manutrack.module.iam.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IamMapper {
    IamDtos.UserResponse toUserResponse(User user);
    IamDtos.AuditLogResponse toAuditLogResponse(AuditLog log);
    void updateUserFromRequest(IamDtos.UpdateUserRequest request, @MappingTarget User user);
}
