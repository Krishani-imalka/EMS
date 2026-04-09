package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.service.FilterEventService;
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
public class FilterEventController {

    @Autowired
    private FilterEventService filterEventService;


    @GetMapping("/filter-events")
    public String filterEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String location,
            Model model) {

        LocalDate from = (dateFrom != null && !dateFrom.isEmpty()) ? LocalDate.parse(dateFrom) : null;
        LocalDate to   = (dateTo   != null && !dateTo.isEmpty())   ? LocalDate.parse(dateTo)   : null;

        List<Event> events = filterEventService.filterEvents(search, category, from, to, location);

        // Distinct locations for dropdown
        List<String> locations = filterEventService.getAllApprovedLocations();

        model.addAttribute("events",    events);
        model.addAttribute("totalEvents", events.size());
        model.addAttribute("locations", locations);
        model.addAttribute("search",    search);
        model.addAttribute("category",  category);
        model.addAttribute("dateFrom",  dateFrom);
        model.addAttribute("dateTo",    dateTo);
        model.addAttribute("location",  location);

        return "FilterEvent";
    }
}
