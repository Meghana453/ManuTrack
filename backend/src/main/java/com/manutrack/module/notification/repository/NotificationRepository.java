////package com.manutrack.module.notification.repository;
////
////import com.manutrack.module.notification.entity.Notification;
////import org.springframework.data.jpa.repository.JpaRepository;
////import org.springframework.stereotype.Repository;
////import java.util.List;
////
////@Repository
////public interface NotificationRepository extends JpaRepository<Notification, Long> {
////    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);
////    List<Notification> findByUserIdAndStatusOrderByCreatedDateDesc(Long userId, Notification.Status status);
////    List<Notification> findByCategory(Notification.Category category);
////    long countByUserIdAndStatus(Long userId, Notification.Status status);
////}
//package com.manutrack.module.notification.repository;
//
//import com.manutrack.module.notification.entity.Notification;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//
//@Repository
//public interface NotificationRepository extends JpaRepository<Notification, Long> {
//    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);
//    List<Notification> findByUserIdAndStatusOrderByCreatedDateDesc(Long userId, Notification.Status status);
//    List<Notification> findByCategory(Notification.Category category);
//    long countByUserIdAndStatus(Long userId, Notification.Status status);
//}
package com.manutrack.module.notification.repository;

import com.manutrack.module.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);

    List<Notification> findByUserIdAndStatusOrderByCreatedDateDesc(Long userId, Notification.Status status);

    List<Notification> findByCategory(Notification.Category category);

    long countByUserIdAndStatus(Long userId, Notification.Status status);
}
