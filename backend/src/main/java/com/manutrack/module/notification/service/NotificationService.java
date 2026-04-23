////package com.manutrack.module.notification.service;
////
////import com.manutrack.exception.ResourceNotFoundException;
////import com.manutrack.module.notification.dto.NotificationDtos;
////import com.manutrack.module.notification.entity.Notification;
////import com.manutrack.module.notification.mapper.NotificationMapper;
////import com.manutrack.module.notification.repository.NotificationRepository;
////import lombok.RequiredArgsConstructor;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////import java.util.List;
////
////@Service @RequiredArgsConstructor @Transactional
////public class NotificationService {
////
////    private final NotificationRepository repo;
////    private final NotificationMapper mapper;
////
////    public NotificationDtos.NotificationResponse create(NotificationDtos.CreateNotificationRequest req) {
////        Notification n = Notification.builder().userId(req.getUserId())
////                .message(req.getMessage()).category(req.getCategory()).build();
////        return mapper.toResponse(repo.save(n));
////    }
////
////    public void sendSystemNotification(Long userId, String message, Notification.Category category) {
////        Notification n = Notification.builder().userId(userId).message(message).category(category).build();
////        repo.save(n);
////    }
////
////    @Transactional(readOnly = true)
////    public List<NotificationDtos.NotificationResponse> getAllNotifications() {
////        return repo.findAll().stream().map(mapper::toResponse).toList();
////    }
////
////    @Transactional(readOnly = true)
////    public List<NotificationDtos.NotificationResponse> getByUser(Long userId) {
////        return repo.findByUserIdOrderByCreatedDateDesc(userId).stream().map(mapper::toResponse).toList();
////    }
////
////    @Transactional(readOnly = true)
////    public List<NotificationDtos.NotificationResponse> getUnreadByUser(Long userId) {
////        return repo.findByUserIdAndStatusOrderByCreatedDateDesc(userId, Notification.Status.UNREAD)
////                .stream().map(mapper::toResponse).toList();
////    }
////
////    @Transactional(readOnly = true)
////    public long countUnread(Long userId) {
////        return repo.countByUserIdAndStatus(userId, Notification.Status.UNREAD);
////    }
////
////    public NotificationDtos.NotificationResponse markAsRead(Long id) {
////        Notification n = findById(id);
////        n.setStatus(Notification.Status.READ);
////        return mapper.toResponse(repo.save(n));
////    }
////
////    public NotificationDtos.NotificationResponse dismiss(Long id) {
////        Notification n = findById(id);
////        n.setStatus(Notification.Status.DISMISSED);
////        return mapper.toResponse(repo.save(n));
////    }
////
////    public void markAllReadForUser(Long userId) {
////        repo.findByUserIdAndStatusOrderByCreatedDateDesc(userId, Notification.Status.UNREAD)
////                .forEach(n -> { n.setStatus(Notification.Status.READ); repo.save(n); });
////    }
////
////    public void delete(Long id) { findById(id); repo.deleteById(id); }
////
////    private Notification findById(Long id) {
////        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
////    }
////}
//package com.manutrack.module.notification.service;
//
//import com.manutrack.exception.ResourceNotFoundException;
//import com.manutrack.module.notification.dto.NotificationDtos;
//import com.manutrack.module.notification.entity.Notification;
//import com.manutrack.module.notification.mapper.NotificationMapper;
//import com.manutrack.module.notification.repository.NotificationRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@Service @RequiredArgsConstructor @Transactional
//public class NotificationService {
//
//    private final NotificationRepository repo;
//    private final NotificationMapper mapper;
//
//    public NotificationDtos.NotificationResponse create(NotificationDtos.CreateNotificationRequest req) {
//        Notification n = Notification.builder().userId(req.getUserId())
//                .message(req.getMessage()).category(req.getCategory()).build();
//        return mapper.toResponse(repo.save(n));
//    }
//
//    public void sendSystemNotification(Long userId, String message, Notification.Category category) {
//        Notification n = Notification.builder().userId(userId).message(message).category(category).build();
//        repo.save(n);
//    }
//
//    @Transactional(readOnly = true)
//    public List<NotificationDtos.NotificationResponse> getAllNotifications() {
//        return repo.findAll().stream().map(mapper::toResponse).toList();
//    }
//
//    @Transactional(readOnly = true)
//    public List<NotificationDtos.NotificationResponse> getByUser(Long userId) {
//        return repo.findByUserIdOrderByCreatedDateDesc(userId).stream().map(mapper::toResponse).toList();
//    }
//
//    @Transactional(readOnly = true)
//    public List<NotificationDtos.NotificationResponse> getUnreadByUser(Long userId) {
//        return repo.findByUserIdAndStatusOrderByCreatedDateDesc(userId, Notification.Status.UNREAD)
//                .stream().map(mapper::toResponse).toList();
//    }
//
//    @Transactional(readOnly = true)
//    public long countUnread(Long userId) {
//        return repo.countByUserIdAndStatus(userId, Notification.Status.UNREAD);
//    }
//
//    public NotificationDtos.NotificationResponse markAsRead(Long id) {
//        Notification n = findById(id);
//        n.setStatus(Notification.Status.READ);
//        return mapper.toResponse(repo.save(n));
//    }
//
//    public NotificationDtos.NotificationResponse dismiss(Long id) {
//        Notification n = findById(id);
//        n.setStatus(Notification.Status.DISMISSED);
//        return mapper.toResponse(repo.save(n));
//    }
//
//    public void markAllReadForUser(Long userId) {
//        repo.findByUserIdAndStatusOrderByCreatedDateDesc(userId, Notification.Status.UNREAD)
//                .forEach(n -> { n.setStatus(Notification.Status.READ); repo.save(n); });
//    }
//
//    public void delete(Long id) { findById(id); repo.deleteById(id); }
//
//    private Notification findById(Long id) {
//        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
//    }
//}
package com.manutrack.module.notification.service;

import com.manutrack.exception.ResourceNotFoundException;
import com.manutrack.module.notification.dto.NotificationDtos;
import com.manutrack.module.notification.entity.Notification;
import com.manutrack.module.notification.mapper.NotificationMapper;
import com.manutrack.module.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationMapper mapper;

    public NotificationDtos.NotificationResponse create(NotificationDtos.CreateNotificationRequest req) {
        Notification n = Notification.builder()
                .userId(req.getUserId())
                .message(req.getMessage())
                .category(req.getCategory())
                .build();
        return mapper.toResponse(repo.save(n));
    }

    public void sendSystemNotification(Long userId, String message, Notification.Category category) {
        Notification n = Notification.builder()
                .userId(userId)
                .message(message)
                .category(category)
                .build();
        repo.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationDtos.NotificationResponse> getAllNotifications() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDtos.NotificationResponse> getByUser(Long userId) {
        return repo.findByUserIdOrderByCreatedDateDesc(userId)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDtos.NotificationResponse> getUnreadByUser(Long userId) {
        return repo.findByUserIdAndStatusOrderByCreatedDateDesc(userId, Notification.Status.UNREAD)
                .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return repo.countByUserIdAndStatus(userId, Notification.Status.UNREAD);
    }

    public NotificationDtos.NotificationResponse markAsRead(Long id) {
        Notification n = findById(id);
        n.setStatus(Notification.Status.READ);
        return mapper.toResponse(repo.save(n));
    }

    public NotificationDtos.NotificationResponse dismiss(Long id) {
        Notification n = findById(id);
        n.setStatus(Notification.Status.DISMISSED);
        return mapper.toResponse(repo.save(n));
    }

    public void markAllReadForUser(Long userId) {
        repo.findByUserIdAndStatusOrderByCreatedDateDesc(userId, Notification.Status.UNREAD)
                .forEach(n -> {
                    n.setStatus(Notification.Status.READ);
                    repo.save(n);
                });
    }

    public void delete(Long id) {
        findById(id);
        repo.deleteById(id);
    }

    private Notification findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
    }
}
