package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/organizer")
public class Organizer_DashboardController {
      @Autowired
     private EventService eventService;


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Event> events = eventService.getAllEvents();

        long total = events.size();
        long approved = events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.APPROVED)
                .count();

        long pending = events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.PENDING)
                .count();

        long rejected = events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.REJECTED)
                .count();

        model.addAttribute("events", events);
        model.addAttribute("total", total);
        model.addAttribute("approved", approved);
        model.addAttribute("pending", pending);
        model.addAttribute("rejected", rejected);



        return "Organizer_Dashboard";
    }



    @GetMapping("/create-event")
    public String createEventPage() {
        return "Organizer_NewEventForm";
    }




}



