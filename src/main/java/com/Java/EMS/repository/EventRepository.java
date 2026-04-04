package com.Java.EMS.repository;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    long countByStatus(Event.EventStatus status);
    List<Event> findTopByStatusOrderByCreatedAtDesc(Event.EventStatus status);
}
