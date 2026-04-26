package com.Java.EMS.repository;

import com.Java.EMS.entity.Feedback;
import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEvent(Event event);
    Optional<Feedback> findByEventAndStudent(Event event, User student);
    List<Feedback> findByEvent_EventId(Long eventId);
    boolean existsByEvent_EventIdAndStudent_Username(Long eventId, String username);
    List<Feedback> findAllByEvent_EventIdAndStudent_Username(Long eventId, String username);
}
