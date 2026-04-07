package com.Java.EMS.controller;

import com.Java.EMS.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/organizer")
public class Organizer_DashboardController {
      @Autowired
    private EventService eventService;

}
