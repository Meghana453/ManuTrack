package com.manutrack.module.notification.mapper;

import com.manutrack.module.notification.dto.NotificationDtos;
import com.manutrack.module.notification.entity.Notification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-26T18:22:21+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDtos.NotificationResponse toResponse(Notification n) {
        if ( n == null ) {
            return null;
        }

        NotificationDtos.NotificationResponse notificationResponse = new NotificationDtos.NotificationResponse();

        notificationResponse.setNotificationId( n.getNotificationId() );
        notificationResponse.setUserId( n.getUserId() );
        notificationResponse.setMessage( n.getMessage() );
        notificationResponse.setCategory( n.getCategory() );
        notificationResponse.setStatus( n.getStatus() );
        notificationResponse.setCreatedDate( n.getCreatedDate() );

        return notificationResponse;
    }
}
