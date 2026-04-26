package com.Java.EMS.service;

import com.Java.EMS.entity.Feedback;
import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.repository.FeedbackRepository;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public Feedback saveFeedback(Long eventId, String username, Integer rating, String comment) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if feedback already exists
        List<Feedback> existing = feedbackRepository.findAllByEvent_EventIdAndStudent_Username(eventId, username);
        Feedback feedback = existing.isEmpty() ? new Feedback() : existing.get(0);

        feedback.setEvent(event);
        feedback.setStudent(student);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setCreatedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackForEvent(Long eventId) {
        return feedbackRepository.findByEvent_EventId(eventId);
    }

    public Double getAverageRating(Long eventId) {
        List<Feedback> feedbacks = feedbackRepository.findByEvent_EventId(eventId);
        if (feedbacks.isEmpty()) {
            return 0.0;
        }
        return feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    public boolean hasStudentGivenFeedback(Long eventId, String username) {
        return feedbackRepository.existsByEvent_EventIdAndStudent_Username(eventId, username);
    }
}
