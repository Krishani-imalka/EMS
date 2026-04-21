package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.entity.Venue;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.repository.UserRepository;
import com.Java.EMS.repository.VenueRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.upload.dir:uploads/banners}")
    private String uploadDir;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizerOrderByCreatedAtDesc(organizer);
    }
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }


    public String updateEventStatus(Long eventId, Event.EventStatus newStatus, String adminRemarks) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "Event not found.";
        }
        event.setStatus(newStatus);
        if (adminRemarks != null && !adminRemarks.isBlank()) {
            event.setAdminRemarks(adminRemarks);
        }
        eventRepository.save(event);


        // ── Send notification to the event organizer ──────────────────────
        User organizer = event.getOrganizer();
        String message;

        if (newStatus == Event.EventStatus.APPROVED) {
            message = "Your event \"" + event.getEventName() + "\" has been APPROVED.";
            if (adminRemarks != null && !adminRemarks.isBlank()) {
                message += " Remarks: " + adminRemarks;
            }
        } else if (newStatus == Event.EventStatus.REJECTED) {
            message = "Your event \"" + event.getEventName() + "\" has been REJECTED.";
            if (adminRemarks != null && !adminRemarks.isBlank()) {
                message += " Reason: " + adminRemarks;
            } else {
                message += " No reason provided.";
            }
        } else {
            message = "Your event \"" + event.getEventName() + "\" status changed to " + newStatus.name() + ".";
        }

        notificationService.sendNotification(organizer, event, message);

        return null; // null = success
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Deletes an event by ID.
     * Returns null on success, or an error message string on failure.
     */
    public String deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            return "Event not found.";
        }
        eventRepository.deleteById(eventId);
        return null; // null = success
    }

    /**
     * Creates and saves a new event.
     * Returns null on success, or an error message string on failure.
     */
    public String createEvent(
            String organizerUserId,
            String eventName,
            String description,
            LocalDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String location,
            String venueName,
            Event.EventCategory category,
            Integer expectedAttendees,
            String contactInfo,
            MultipartFile bannerImage
    ) {
        // 1. Load organizer
        User organizer = userRepository.findById(organizerUserId).orElse(null);
        if (organizer == null) {
            return "Organizer not found.";
        }

        // 2. Validate end time is after start time
        if (!endTime.isAfter(startTime)) {
            return "End time must be after start time.";
        }

        // 3. Duplicate check (same organizer + name + date)
        if (eventRepository.existsByOrganizerAndEventNameAndEventDate(organizer, eventName, eventDate)) {
            return "You already have an event with this name on that date.";
        }

        // 4. Resolve venue (optional)
        Venue venue = null;
        if (venueName != null && !venueName.isBlank()) {
            venue = venueRepository.findById(venueName).orElse(null);

            // 5. Venue time clash check
            if (venue != null && eventRepository.existsVenueTimeClash(venue, eventDate, startTime, endTime)) {
                return "This venue is already booked for an overlapping time on that date.";
            }
        }

        // 6. Handle banner image upload
//        String savedImagePath = null;
//        if (bannerImage != null && !bannerImage.isEmpty()) {
//            try {
//                Path uploadPath = Paths.get(uploadDir);
//                Files.createDirectories(uploadPath);
//                String fileName = UUID.randomUUID() + "_" + bannerImage.getOriginalFilename();
//                Path filePath = uploadPath.resolve(fileName);
//                Files.copy(bannerImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//                savedImagePath = fileName;
//            } catch (IOException e) {
//                return "Failed to upload banner image: " + e.getMessage();
//            }
//        }

        String savedImagePath = saveBannerImage(bannerImage);
        if (savedImagePath != null && savedImagePath.startsWith("ERROR:")) {
            return savedImagePath.substring(6);
        }

        // 7. Build and save Event
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setEventName(eventName);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setLocation(location);
        event.setVenue(venue);
        event.setCategory(category);
        event.setExpectedAttendees(expectedAttendees);
        event.setContactInfo(contactInfo);
        event.setBannerImage(savedImagePath);
        event.setStatus(Event.EventStatus.PENDING);

        eventRepository.save(event);
        return null; // null = success
    }


    // ── UPDATE (EDIT) ─────────────────────────────────────────────────────────

    /**
     * Updates an existing event.
     * Returns null on success, or an error message string on failure.
     */
    public String updateEvent(
            Long eventId,
            String eventName,
            String description,
            LocalDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String location,
            String venueName,
            Event.EventCategory category,
            Integer expectedAttendees,
            String contactInfo,
            MultipartFile bannerImage
    ) {
        // 1. Load existing event
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "Event not found.";
        }

        // 2. Validate end time
        if (!endTime.isAfter(startTime)) {
            return "End time must be after start time.";
        }

        // 3. Duplicate check (excluding self)
        if (eventRepository.existsByOrganizerAndEventNameAndEventDateAndEventIdNot(
                event.getOrganizer(), eventName, eventDate, eventId)) {
            return "You already have another event with this name on that date.";
        }

        // 4. Resolve venue (optional)
        Venue venue = null;
        if (venueName != null && !venueName.isBlank()) {
            venue = venueRepository.findById(venueName).orElse(null);
            if (venue != null && eventRepository.existsVenueTimeClashExcluding(
                    venue, eventDate, startTime, endTime, eventId)) {
                return "This venue is already booked for an overlapping time on that date.";
            }
        }

        // 5. Handle banner image (only replace if a new file was uploaded)
        if (bannerImage != null && !bannerImage.isEmpty()) {
            String savedImagePath = saveBannerImage(bannerImage);
            if (savedImagePath != null && savedImagePath.startsWith("ERROR:")) {
                return savedImagePath.substring(6);
            }
            event.setBannerImage(savedImagePath);
        }

        // 6. Apply changes
        event.setEventName(eventName);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setLocation(location);
        event.setVenue(venue);
        event.setCategory(category);
        event.setExpectedAttendees(expectedAttendees);
        event.setContactInfo(contactInfo);

        eventRepository.save(event);
        return null;
    }

    // ── HELPER ────────────────────────────────────────────────────────────────

    private String saveBannerImage(MultipartFile bannerImage) {
        if (bannerImage == null || bannerImage.isEmpty()) return null;
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            String fileName = UUID.randomUUID() + "_" + bannerImage.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(bannerImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            return "ERROR: Failed to upload banner image: " + e.getMessage();
        }
    }

    public java.util.Set<Long> getClashingEventIds() {
        List<Event> allEvents = eventRepository.findByStatus(Event.EventStatus.PENDING);
        java.util.Set<Long> clashingIds = new java.util.HashSet<>();

        for (Event event : allEvents) {
            if (event.getVenue() == null) continue;
            List<Event> clashes = eventRepository.findClashingPendingEvents(
                    event.getVenue(),
                    event.getEventDate(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getEventId()
            );
            if (!clashes.isEmpty()) {
                clashingIds.add(event.getEventId());
            }
        }
        return clashingIds;
    }



    // ─── Get events by organizer ──────────────────────────────────────────────
    public List<Event> getEventsByOrganizer(String organizerUserId) {
        return eventRepository.findByOrganizer_UserId(organizerUserId); // ✅ fixed
    }

    // ─── Get single event (ownership check) ──────────────────────────────────
    public Event getEventByIdAndOrganizer(Long eventId, String organizerUserId) { // ✅ Long
        return eventRepository.findByEventIdAndOrganizer_UserId(eventId, organizerUserId)
                .orElse(null);
    }

    // ─── Delete event (organizer-scoped) ─────────────────────────────────────
    public String deleteEvent(Long eventId, String organizerUserId) { // ✅ Long
        Event event = eventRepository.findByEventIdAndOrganizer_UserId(eventId, organizerUserId)
                .orElse(null);
        if (event == null) return "Event not found or unauthorized.";
        eventRepository.delete(event);
        return null;
    }

    // ─── Update event (organizer-scoped) ─────────────────────────────────────
    public String updateEvent(
            Long eventId, String organizerUserId, // ✅ Long
            String eventName, String description,
            LocalDate eventDate, LocalTime startTime, LocalTime endTime,
            String location, String venueName,
            Event.EventCategory category, Integer expectedAttendees,
            String contactInfo, MultipartFile bannerImage) {

        Event event = eventRepository.findByEventIdAndOrganizer_UserId(eventId, organizerUserId)
                .orElse(null);
        if (event == null) return "Event not found or unauthorized.";

        if (event.getStatus() != Event.EventStatus.PENDING) {
            return "Only pending events can be edited.";
        }

        event.setEventName(eventName);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setLocation(location);

        if (venueName != null && !venueName.isBlank()) {
            Venue venue = venueRepository.findByVName(venueName).orElse(null);
            event.setVenue(venue);
        }

        event.setCategory(category);
        event.setExpectedAttendees(expectedAttendees);
        event.setContactInfo(contactInfo);

        if (bannerImage != null && !bannerImage.isEmpty()) {
            String savedPath = saveBannerImage(bannerImage);
            if (savedPath != null && savedPath.startsWith("ERROR:")) {
                return savedPath.substring(6);
            }
            event.setBannerImage(savedPath);
        }

        eventRepository.save(event);
        return null;
    }

}
