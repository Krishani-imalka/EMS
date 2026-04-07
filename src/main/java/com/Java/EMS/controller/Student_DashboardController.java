package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.service.Student_DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student")
public class Student_DashboardController {

    @Autowired
    private Student_DashboardService studentDashboardService;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String search,
            Model model) {

        List<Event> featuredEvents;
        List<Event> scheduleEvents;

        if (search != null && !search.isEmpty()) {
            // Split search results by 2-week boundary
            LocalDate twoWeeksLater = LocalDate.now().plusDays(14);
            List<Event> results = studentDashboardService.searchEvents(search);

            featuredEvents = results.stream()
                    .filter(e -> !e.getEventDate().isAfter(twoWeeksLater))
                    .toList();

            scheduleEvents = results.stream()
                    .filter(e -> e.getEventDate().isAfter(twoWeeksLater))
                    .toList();
        } else {
            // No search — load from DB with date logic
            featuredEvents = studentDashboardService.getFeaturedEvents();
            scheduleEvents = studentDashboardService.getUpcomingEvents();
        }

        model.addAttribute("featuredEvents", featuredEvents);
        model.addAttribute("scheduleEvents", scheduleEvents);
        model.addAttribute("search", search);

        return "Student_Dashboard";
    }

    @GetMapping("/Filterevents")
    public String events(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Model model) {

        List<Event> events;

        if (search != null && !search.isEmpty()) {
            events = studentDashboardService.searchEvents(search);
        } else if (category != null && !category.isEmpty()) {
            events = studentDashboardService.getEventsByCategory(category);
        } else {
            events = studentDashboardService.getApprovedEvents();
        }

        model.addAttribute("events", events);
        model.addAttribute("search", search);
        model.addAttribute("category", category);

        return "Student_Dashboard";
    }
}