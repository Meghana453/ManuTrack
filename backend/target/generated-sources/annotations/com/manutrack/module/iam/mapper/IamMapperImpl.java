package com.manutrack.module.iam.mapper;

import com.manutrack.module.iam.dto.IamDtos;
import com.manutrack.module.iam.entity.AuditLog;
import com.manutrack.module.iam.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-26T18:22:21+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class IamMapperImpl implements IamMapper {

    @Override
    public IamDtos.UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        IamDtos.UserResponse userResponse = new IamDtos.UserResponse();

        userResponse.setUserId( user.getUserId() );
        userResponse.setName( user.getName() );
        userResponse.setRole( user.getRole() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setPhone( user.getPhone() );
        userResponse.setActive( user.isActive() );
        userResponse.setCreatedAt( user.getCreatedAt() );

        return userResponse;
    }

    @Override
    public IamDtos.AuditLogResponse toAuditLogResponse(AuditLog log) {
        if ( log == null ) {
            return null;
        }

        IamDtos.AuditLogResponse auditLogResponse = new IamDtos.AuditLogResponse();

        auditLogResponse.setAuditId( log.getAuditId() );
        auditLogResponse.setUserId( log.getUserId() );
        auditLogResponse.setAction( log.getAction() );
        auditLogResponse.setResource( log.getResource() );
        auditLogResponse.setTimestamp( log.getTimestamp() );
        auditLogResponse.setMetadata( log.getMetadata() );

        return auditLogResponse;
    }

    @Override
    public void updateUserFromRequest(IamDtos.UpdateUserRequest request, User user) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            user.setName( request.getName() );
        }
        if ( request.getRole() != null ) {
            user.setRole( request.getRole() );
        }
        if ( request.getEmail() != null ) {
            user.setEmail( request.getEmail() );
        }
        if ( request.getPhone() != null ) {
            user.setPhone( request.getPhone() );
        }
        if ( request.getActive() != null ) {
            user.setActive( request.getActive() );
        }
    }
}
