package com.Java.EMS.controller;

import com.Java.EMS.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/organizer")
public class Organizer_DashboardController {
      @Autowired
     private EventService eventService;


    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String search,
            Model model) {



        return "Organizer_Dashboard";
    }


    @PostMapping("/createEvent")
    public String Event(
            @RequestParam(required = false) String search,
            Model model) {



        return "Organizer_NewEventForm";
    }

    @GetMapping("/ Allevents")
    public String AllEvent(
            @RequestParam(required = false) String search,
            Model model) {



        return "Organizer_AllEvent";
    }


}



