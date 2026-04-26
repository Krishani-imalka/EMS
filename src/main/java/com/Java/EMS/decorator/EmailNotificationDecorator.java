package com.Java.EMS.decorator;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.service.INotificationService;

public class EmailNotificationDecorator extends NotificationDecorator {

    public EmailNotificationDecorator(INotificationService decoratedService) {
        super(decoratedService);
    }

    @Override
    public void sendNotification(User user, Event event, String message) {
        super.sendNotification(user, event, message);
        sendEmail(user.getEmail(), "Notification: " + event.getEventName(), message);
    }

    private void sendEmail(String to, String subject, String body) {
        // Simulated email sending logic
        System.out.println("SIMULATED EMAIL SENT TO: " + to);
        System.out.println("SUBJECT: " + subject);
        System.out.println("BODY: " + body);
    }
}
