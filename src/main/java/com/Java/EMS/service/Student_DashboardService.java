package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.entity.User;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.repository.Event_RegisterRepository;
import com.Java.EMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class Student_DashboardService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Event_RegisterRepository eventRegisterRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatusOrderByEventDateAsc(Event.EventStatus.APPROVED);
    }


    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchApprovedEvents(keyword);
    }


    public List<Event> getEventsByCategory(String category) {
        Event.EventCategory cat = Event.EventCategory.valueOf(category.toUpperCase());
        return eventRepository.findByStatusAndCategoryOrderByEventDateAsc(
                Event.EventStatus.APPROVED, cat);
    }

    public List<Event> getFeaturedEvents() {
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksLater = today.plusDays(14);
        return eventRepository.findApprovedEventsInNextTwoWeeks(today, twoWeeksLater);
    }

    public List<Event> getUpcomingEvents() {
        LocalDate twoWeeksLater = LocalDate.now().plusDays(14);
        return eventRepository.findApprovedEventsAfterTwoWeeks(twoWeeksLater);
    }

    public List<Event_Registation> getMyRegistrations(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return eventRegisterRepository.findByStudentOrderByRegisteredAtDesc(student);
    }
}
