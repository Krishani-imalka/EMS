package com.Java.EMS.decorator;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.service.INotificationService;

public abstract class NotificationDecorator implements INotificationService {
    protected INotificationService decoratedService;

    public NotificationDecorator(INotificationService decoratedService) {
        this.decoratedService = decoratedService;
    }

    @Override
    public void sendNotification(User user, Event event, String message) {
        decoratedService.sendNotification(user, event, message);
    }
}
