////package com.manutrack.module.notification.mapper;
////
////import com.manutrack.module.notification.dto.NotificationDtos;
////import com.manutrack.module.notification.entity.Notification;
////import org.mapstruct.Mapper;
////
////@Mapper(componentModel = "spring")
////public interface NotificationMapper {
////    NotificationDtos.NotificationResponse toResponse(Notification n);
////}
//package com.manutrack.module.notification.mapper;
//
//import com.manutrack.module.notification.dto.NotificationDtos;
//import com.manutrack.module.notification.entity.Notification;
//import org.mapstruct.Mapper;
//
//@Mapper(componentModel = "spring")
//public interface NotificationMapper {
//    NotificationDtos.NotificationResponse toResponse(Notification n);
//}
package com.manutrack.module.notification.mapper;

import com.manutrack.module.notification.dto.NotificationDtos;
import com.manutrack.module.notification.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDtos.NotificationResponse toResponse(Notification n);
}
