package com.Java.EMS.service;

import com.Java.EMS.repository.Event_RegisterRepository;
import org.springframework.stereotype.Service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.entity.User;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> getDistinctDepartments() {
        return userRepository.findDistinctDepartments();
    }

    // Load only APPROVED events for the dropdown
    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatus(Event.EventStatus.APPROVED)
                .stream()
                .filter(e -> e.getEventDate() == null || !e.getEventDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }


    @Transactional
    public Event_Registation registerStudentForEvent(Long eventId,String userId, String username) {

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + username));


        if (!student.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "User ID does not match your account. Please enter your own User ID.");
        }

        if (student.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException("Only students can register for events.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        if (event.getStatus() != Event.EventStatus.APPROVED) {
            throw new IllegalStateException("This event is no longer open for registration.");
        }

        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException(
                    "Registration closed. This event took place on " + event.getEventDate() + ".");
        }

        if (registrationRepository.existsByEventAndStudent(event, student)) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Event_Registation.RegistrationStatus status;

        if (event.getExpectedAttendees() != null) {
            long registeredCount = registrationRepository.countByEventAndRegistrationStatus(
                    event, Event_Registation.RegistrationStatus.REGISTERED);

            if (registeredCount < event.getExpectedAttendees()) {
                status = Event_Registation.RegistrationStatus.REGISTERED;
            } else {
                status = Event_Registation.RegistrationStatus.PENDING;
            }
        } else {
            status = Event_Registation.RegistrationStatus.REGISTERED;
        }


        Event_Registation registration = new Event_Registation();
        registration.setEvent(event);
        registration.setStudent(student);
        registration.setRegistrationStatus(status);


        return registrationRepository.save(registration);
    }
}
