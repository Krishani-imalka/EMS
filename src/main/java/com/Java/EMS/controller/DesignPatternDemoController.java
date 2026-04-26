package com.Java.EMS.controller;

import com.Java.EMS.decorator.EmailNotificationDecorator;
import com.Java.EMS.decorator.LoggingNotificationDecorator;
import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.service.INotificationService;
import com.Java.EMS.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DesignPatternDemoController {

    @Autowired
    private NotificationService baseNotificationService;


    @GetMapping("/decorator")
    public String demoDecorator() {
        // Create an event and user for demo
        User user = new User();
        user.setUsername("decorator_user");
        user.setEmail("user@example.com");

        Event event = new Event();
        event.setEventName("Design Pattern Workshop");

        // Use Decorators to wrap the base service
        INotificationService decoratedService = new LoggingNotificationDecorator(
                                                    new EmailNotificationDecorator(baseNotificationService));

        decoratedService.sendNotification(user, event, "Hello! This notification is decorated with Logging and Email logic.");

        return "Decorator Pattern: Notification sent using LoggingNotificationDecorator(EmailNotificationDecorator(BaseService)). Check console for logs!";
    }
}
