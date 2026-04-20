package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Notifications;
import com.Java.EMS.entity.User;
import com.Java.EMS.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(User user, Event event, String message) {
        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setEvent(event);
        notification.setEventName(event.getEventName());
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}
