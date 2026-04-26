package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilterEventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> filterEvents(String search, String category, LocalDate from, LocalDate to, String location) {

        Event.EventCategory cat = null;
        if (category != null && !category.isEmpty()) {
            try {
                cat = Event.EventCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        String s   = (search   != null && !search.isEmpty())   ? search   : null;
        String loc = (location != null && !location.isEmpty()) ? location : null;

        // get the filter data from database and return controller
        return eventRepository.filterApprovedEvents(search, cat, from, to, location);
    }

    public List<String> getAllApprovedLocations() {
        return eventRepository.findDistinctApprovedLocations();
    }
}
