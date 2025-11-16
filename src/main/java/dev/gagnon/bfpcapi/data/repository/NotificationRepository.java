package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n where n.recipient.email=:email")
    List<Notification> getNotificationByUserEmail(String email);
}
