package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.entity.User;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.repository.Event_RegisterRepository;
import com.Java.EMS.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantsService {
    private final Event_RegisterRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ParticipantsService(Event_RegisterRepository registrationRepository,
                               EventRepository eventRepository,
                               UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    // Get logged-in organizer
    public User getLoggedInUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    // Get all events owned by this organizer
    public List<Event> getOrganizerEvents(String username) {
        User organizer = getLoggedInUser(username);
        return eventRepository.findByOrganizerOrderByEventDateDesc(organizer);
    }

    // Get all registrations for all events owned by this organizer
    public List<Event_Registation> getAllParticipants(String username) {
        User organizer = getLoggedInUser(username);
        return registrationRepository.findByEvent_OrganizerOrderByRegisteredAtDesc(organizer);
    }

    // Get registrations filtered by a specific event
    public List<Event_Registation> getParticipantsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        return registrationRepository.findByEventOrderByRegisteredAtDesc(event);
    }

    // Count stats
    public long countRegistered(List<Event_Registation> list) {
        return list.stream()
                .filter(r -> r.getRegistrationStatus() == Event_Registation.RegistrationStatus.REGISTERED)
                .count();
    }

    public long countPending(List<Event_Registation> list) {
        return list.stream()
                .filter(r -> r.getRegistrationStatus() == Event_Registation.RegistrationStatus.PENDING)
                .count();
    }

    public long countCancelled(List<Event_Registation> list) {
        return list.stream()
                .filter(r -> r.getRegistrationStatus() == Event_Registation.RegistrationStatus.CANCELLED)
                .count();
    }
}
