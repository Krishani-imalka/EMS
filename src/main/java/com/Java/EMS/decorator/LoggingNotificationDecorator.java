package com.Java.EMS.decorator;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingNotificationDecorator extends NotificationDecorator {
    private static final Logger logger = LoggerFactory.getLogger(LoggingNotificationDecorator.class);

    public LoggingNotificationDecorator(INotificationService decoratedService) {
        super(decoratedService);
    }

    @Override
    public void sendNotification(User user, Event event, String message) {
        logger.info("Sending notification to user: {} for event: {}", user.getUsername(), event.getEventName());
        super.sendNotification(user, event, message);
        logger.info("Notification successfully processed for user: {}", user.getUsername());
    }
}
