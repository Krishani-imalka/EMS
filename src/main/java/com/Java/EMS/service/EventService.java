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

    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizerOrderByCreatedAtDesc(organizer);
    }
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
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
        String savedImagePath = null;
        if (bannerImage != null && !bannerImage.isEmpty()) {
            try {
                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);
                String fileName = UUID.randomUUID() + "_" + bannerImage.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(bannerImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                savedImagePath = fileName;
            } catch (IOException e) {
                return "Failed to upload banner image: " + e.getMessage();
            }
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

}
