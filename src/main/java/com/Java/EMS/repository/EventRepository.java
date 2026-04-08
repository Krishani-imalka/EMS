package com.Java.EMS.repository;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    long countByStatus(Event.EventStatus status);
    List<Event> findTopByStatusOrderByCreatedAtDesc(Event.EventStatus status);

    List<Event> findByStatusOrderByEventDateAsc(Event.EventStatus status);

    List<Event> findByStatusAndCategoryOrderByEventDateAsc(
            Event.EventStatus status,
            Event.EventCategory category);


    @Query("SELECT e FROM Event e WHERE e.status = 'APPROVED' AND (" +
            "LOWER(e.eventName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.location)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CAST(e.category AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> searchApprovedEvents(@Param("keyword") String keyword);


    // Featured: approved events within next 14 days
    @Query("SELECT e FROM Event e WHERE e.status = 'APPROVED' " +
            "AND e.eventDate >= :today AND e.eventDate <= :twoWeeksLater " +
            "ORDER BY e.eventDate ASC")
    List<Event> findApprovedEventsInNextTwoWeeks(
            @Param("today") LocalDate today,
            @Param("twoWeeksLater") LocalDate twoWeeksLater);

    // Upcoming: approved events AFTER the next 2 weeks
    @Query("SELECT e FROM Event e WHERE e.status = 'APPROVED' " +
            "AND e.eventDate > :twoWeeksLater " +
            "ORDER BY e.eventDate ASC")
    List<Event> findApprovedEventsAfterTwoWeeks(
            @Param("twoWeeksLater") LocalDate twoWeeksLater);

    // Add these to EventRepository.java

    @Query("SELECT e FROM Event e WHERE e.status = 'APPROVED' " +
            "AND (:search IS NULL OR " +
            "     LOWER(e.eventName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(e.location)  LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(CAST(e.category AS string)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:category IS NULL OR e.category = :category) " +
            "AND (:from     IS NULL OR e.eventDate >= :from) " +
            "AND (:to       IS NULL OR e.eventDate <= :to) " +
            "AND (:location IS NULL OR e.location LIKE CONCAT('%', :location, '%')) " +
            "ORDER BY e.eventDate ASC")
    List<Event> filterApprovedEvents(
            @Param("search")   String search,
            @Param("category") Event.EventCategory category,
            @Param("from")     LocalDate from,
            @Param("to")       LocalDate to,
            @Param("location") String location);

    @Query("SELECT DISTINCT e.location FROM Event e " +
            "WHERE e.status = 'APPROVED' ORDER BY e.location ASC")
    List<String> findDistinctApprovedLocations();

}
