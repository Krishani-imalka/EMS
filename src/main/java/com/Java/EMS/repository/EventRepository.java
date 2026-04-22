package com.Java.EMS.repository;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.entity.Venue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    long countByStatus(Event.EventStatus status);
    long countByOrganizer(User organizer);
    long countByOrganizerAndStatus(User organizer, Event.EventStatus status);


    List<Event> findByStatusOrderByCreatedAtDesc(Event.EventStatus status);
    List<Event> findByStatusOrderByEventDateAsc(Event.EventStatus status);
    List<Event> findByStatusAndCategoryOrderByEventDateAsc(Event.EventStatus status, Event.EventCategory category);
    List<Event> findByOrganizerOrderByCreatedAtDesc(User organizer);
    List<Event> findByOrganizerOrderByCreatedAtDesc(User organizer, Pageable pageable);
    List<Event> findByStatus(Event.EventStatus status);


    List<Event> findByOrganizer_UserId(String organizerUserId);
    Optional<Event> findByEventIdAndOrganizer_UserId(Long eventId, String organizerUserId);
    List<Event> findByOrganizer_UserIdAndStatus(String organizerUserId, Event.EventStatus status);

    List<Event> findByOrganizerOrderByEventDateDesc(User organizer);

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
            @Param("search") String search,
            @Param("category") Event.EventCategory category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("location") String location);

    @Query("SELECT DISTINCT e.location FROM Event e " +
            "WHERE e.status = 'APPROVED' ORDER BY e.location ASC")
    List<String> findDistinctApprovedLocations();


    // ── Organizer: duplicate check (create)
    boolean existsByOrganizerAndEventNameAndEventDate(
            User organizer, String eventName, LocalDate eventDate);

    // ── Organizer: duplicate check (edit — excludes self)
    boolean existsByOrganizerAndEventNameAndEventDateAndEventIdNot(
            User organizer, String eventName, LocalDate eventDate, Long eventId);


    @Query("""
        SELECT COUNT(e) > 0 FROM Event e
        WHERE e.venue = :venue
          AND e.eventDate = :eventDate
          AND e.status = com.Java.EMS.entity.Event.EventStatus.APPROVED
          AND e.startTime < :endTime
          AND e.endTime   > :startTime
    """)
    boolean existsVenueTimeClash(
            @Param("venue")     Venue venue,
            @Param("eventDate") LocalDate eventDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime")   LocalTime endTime);


    @Query("""
        SELECT COUNT(e) > 0 FROM Event e
        WHERE e.venue = :venue
          AND e.eventDate = :eventDate
          AND e.eventId  <> :eventId
          AND e.status = com.Java.EMS.entity.Event.EventStatus.APPROVED
          AND e.startTime < :endTime
          AND e.endTime   > :startTime
    """)
    boolean existsVenueTimeClashExcluding(
            @Param("venue")     Venue venue,
            @Param("eventDate") LocalDate eventDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime")   LocalTime endTime,
            @Param("eventId")   Long eventId);


    @Query("""
    SELECT e FROM Event e
    WHERE e.venue = :venue
      AND e.eventDate = :eventDate
      AND e.status = com.Java.EMS.entity.Event.EventStatus.PENDING
      AND e.startTime < :endTime
      AND e.endTime   > :startTime
      AND e.eventId  <> :excludeId
""")
    List<Event> findClashingPendingEvents(
            @Param("venue")     Venue venue,
            @Param("eventDate") LocalDate eventDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime")   LocalTime endTime,
            @Param("excludeId") Long excludeId);
}

