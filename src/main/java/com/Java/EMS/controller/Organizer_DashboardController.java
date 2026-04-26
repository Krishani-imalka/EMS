package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.entity.Venue;
import com.Java.EMS.repository.UserRepository;
import com.Java.EMS.service.EventService;
import com.Java.EMS.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/organizer")
public class Organizer_DashboardController {
    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackService feedbackService;


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String organizerUserId = (String) session.getAttribute("loggedInUserId");
        if (organizerUserId == null) {
            return "redirect:/login";
        }

        User organizer = userRepository.findById(organizerUserId).orElse(null);
        if (organizer == null) {
            return "redirect:/login";
        }


        List<Event> events = eventService.getAllEvents();

        long total = events.size();
        long approved = events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.APPROVED)
                .count();

        long pending = events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.PENDING)
                .count();

        long rejected = events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.REJECTED)
                .count();

        model.addAttribute("events", events);
        model.addAttribute("total", total);
        model.addAttribute("approved", approved);
        model.addAttribute("pending", pending);
        model.addAttribute("rejected", rejected);



        return "Organizer_Dashboard";
    }


    @GetMapping("/createEvent")
    public String showCreateEventForm(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }
        model.addAttribute("venues",     eventService.getAllVenues());
        model.addAttribute("categories", Event.EventCategory.values());
        return "Organizer_NewEventForm";
    }


    @PostMapping("/createEvent")
    public String createEvent(
            @RequestParam("eventName")         String eventName,
            @RequestParam("description")       String description,
            @RequestParam("eventDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
            @RequestParam("startTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam("location")          String location,
            @RequestParam(value = "venueName", required = false) String venueName,
            @RequestParam("category")          Event.EventCategory category,
            @RequestParam("expectedAttendees") Integer expectedAttendees,
            @RequestParam("contactInfo")       String contactInfo,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String organizerUserId = (String) session.getAttribute("loggedInUserId");

        if (organizerUserId == null) {
            return "redirect:/login";
        }

        String error = eventService.createEvent(
                organizerUserId,
                eventName, description,
                eventDate, startTime, endTime,
                location, venueName,
                category, expectedAttendees,
                contactInfo, bannerImage
        );

        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/organizer/createEvent";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Event created successfully and is pending approval.");
        return "redirect:/organizer/dashboard";
    }



    @GetMapping("/Allevents")
    public String showAllEvents(HttpSession session, Model model) {
        String organizerUserId = (String) session.getAttribute("loggedInUserId");
        if (organizerUserId == null) return "redirect:/login";

        List<Event> events = eventService.getEventsByOrganizer(organizerUserId);
        model.addAttribute("events", events);

        java.util.Map<Long, Double> averageRatings = new java.util.HashMap<>();
        java.util.Map<Long, List<com.Java.EMS.entity.Feedback>> eventFeedbacks = new java.util.HashMap<>();

        for (Event event : events) {
            averageRatings.put(event.getEventId(), feedbackService.getAverageRating(event.getEventId()));
            eventFeedbacks.put(event.getEventId(), feedbackService.getFeedbackForEvent(event.getEventId()));
        }

        model.addAttribute("averageRatings", averageRatings);
        model.addAttribute("eventFeedbacks", eventFeedbacks);

        model.addAttribute("venues", eventService.getAllVenues());
        model.addAttribute("categories", Event.EventCategory.values());

        return "Organizer_AllEvent";
    }


    @PostMapping("/deleteEvent/{eventId}")
    public String deleteEvent(@PathVariable Long eventId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String organizerUserId = (String) session.getAttribute("loggedInUserId");
        if (organizerUserId == null) return "redirect:/login";

        String error = eventService.deleteEvent(eventId, organizerUserId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        }
        return "redirect:/organizer/Allevents";
    }

    @GetMapping("/editEvent/{eventId}")
    public String showEditForm(@PathVariable Long eventId,
                               HttpSession session, Model model) {
        String organizerUserId = (String) session.getAttribute("loggedInUserId");
        if (organizerUserId == null) return "redirect:/login";

        Event event = eventService.getEventByIdAndOrganizer(eventId, organizerUserId);
        if (event == null) return "redirect:/organizer/Allevents";

        model.addAttribute("event", event);
        model.addAttribute("venues", eventService.getAllVenues());
        model.addAttribute("categories", Event.EventCategory.values());
        return "Organizer_EditEventForm";
    }

    @PostMapping("/editEvent/{eventId}")
    public String updateEvent(
            @PathVariable Long eventId,
            @RequestParam("eventName")         String eventName,
            @RequestParam("description")       String description,
            @RequestParam("eventDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
            @RequestParam("startTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam("location")          String location,
            @RequestParam(value = "venueName", required = false) String venueName,
            @RequestParam("category")          Event.EventCategory category,
            @RequestParam("expectedAttendees") Integer expectedAttendees,
            @RequestParam("contactInfo")       String contactInfo,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String organizerUserId = (String) session.getAttribute("loggedInUserId");
        if (organizerUserId == null) return "redirect:/login";

        String error = eventService.updateEvent(
                eventId, organizerUserId,
                eventName, description,
                eventDate, startTime, endTime,
                location, venueName,
                category, expectedAttendees,
                contactInfo, bannerImage
        );

        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
            return "redirect:/organizer/editEvent/" + eventId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully.");
        return "redirect:/organizer/Allevents";
    }

    @GetMapping("/viewEvent/{eventId}")
    public String viewEvent(@PathVariable Long eventId,
                            HttpSession session, Model model) {
        String organizerUserId = (String) session.getAttribute("loggedInUserId");
        if (organizerUserId == null) return "redirect:/login";

        Event event = eventService.getEventByIdAndOrganizer(eventId, organizerUserId);
        if (event == null) return "redirect:/organizer/Allevents";

        model.addAttribute("event", event);
        return "Organizer_ViewEvent";
    }

}



