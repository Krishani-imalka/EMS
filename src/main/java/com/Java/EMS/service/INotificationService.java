package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;

public interface INotificationService {
    void sendNotification(User user, Event event, String message);
}
