package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
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
class Admin_DashboardServiceImpl implements Admin_DashboardService {

    @Autowired
    private Admin_DashboardRepository adminDashboardRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

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
        return eventRepository.findTopByStatusOrderByCreatedAtDesc(Event.EventStatus.PENDING);
    }

    @Override
    public List<User> getPendingUsers() {
        return userRepository.findTopByStatusOrderByUserIdDesc(User.Status.PENDING);
    }

    @Override
    public void activeUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
    }


    @Override
    public void approvedEvent(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
        event.setStatus(Event.EventStatus.APPROVED);
        eventRepository.save(event);
    }

}