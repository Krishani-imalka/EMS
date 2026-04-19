package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.repository.Event_RegisterRepository;
import com.Java.EMS.entity.User;
import com.Java.EMS.repository.Admin_DashboardRepository;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class Admin_DashboardServiceImpl implements Admin_DashboardService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Admin_DashboardServiceImpl(EventRepository eventRepository,
                                      UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository  = userRepository;
    }

    @Autowired
    private Event_RegisterRepository eventRegistrationRepository;

    @Override
    public List<Event_Registation> getRecentEventRegistrations() {
        return eventRegistrationRepository
                .findTop10ByRegistrationStatusOrderByRegisteredAtDesc(
                        Event_Registation.RegistrationStatus.REGISTERED);
    }

    @Override
    public void updateRegistrationStatus(Long registrationId, Event_Registation.RegistrationStatus status) {
        Event_Registation reg = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found: " + registrationId));
        reg.setRegistrationStatus(status);
        eventRegistrationRepository.save(reg);
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public long getTotalEvents() {
        return eventRepository.count();
    }

    @Override
    public long getActiveEventCount() {
        return eventRepository.countByStatus(Event.EventStatus.APPROVED);
    }

    @Override
    public long getPendingApprovalCount() {
        return eventRepository.countByStatus(Event.EventStatus.PENDING);
    }

    @Override
    public List<Event> getPendingEvents() {
        return eventRepository.findByStatusOrderByCreatedAtDesc(Event.EventStatus.PENDING);
    }

    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByStatusOrderByUserIdDesc(User.Status.PENDING);

    }

    @Override
    public void activeUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void approvedEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
        event.setStatus(Event.EventStatus.APPROVED);
        eventRepository.save(event);
    }

}