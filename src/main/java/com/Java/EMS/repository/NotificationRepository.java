package com.Java.EMS.repository;

import com.Java.EMS.entity.Notifications;
import com.Java.EMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository  extends JpaRepository<Notifications, Long> {
    List<Notifications> findByUserOrderByCreatedAtDesc(User user);
}
