package com.Java.EMS.service;

import com.Java.EMS.repository.Event_RegisterRepository;
import org.springframework.stereotype.Service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.entity.User;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class Event_RegisterService {
    private final EventRepository eventRepository;
    private final Event_RegisterRepository registrationRepository;
    private final UserRepository userRepository;

    public Event_RegisterService(EventRepository eventRepository,
                                    Event_RegisterRepository registrationRepository,
                                    UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    public User getLoggedInUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    // Load only APPROVED events for the dropdown
    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatus(Event.EventStatus.APPROVED);
    }

    // Register a student for an event
    @Transactional
    public Event_Registation registerStudentForEvent(Long eventId, String username) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        if (event.getStatus() != Event.EventStatus.APPROVED) {
            throw new IllegalStateException("Event is not open for registration.");
        }

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + username));

        if (registrationRepository.existsByEventAndStudent(event, student)) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Event_Registation registration = new Event_Registation();
        registration.setEvent(event);
        registration.setStudent(student);


        return registrationRepository.save(registration);
    }
}
