package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.service.EventService;
import com.Java.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping({"/events"})
    public String eventManagement(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "Event_management";
    }

}
