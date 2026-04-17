package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.entity.Venue;
import com.Java.EMS.repository.UserRepository;
import com.Java.EMS.service.EventService;
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


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
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
    public String showCreateEventForm(Model model) {
        List<Venue> venues = eventService.getAllVenues();
        model.addAttribute("venues",     venues);
        model.addAttribute("categories", Event.EventCategory.values());
        return "Organizer_NewEventForm";
    }

    // ─── Handle form submission ───────────────────────────────────────────────
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
        // ── Get logged-in organizer from session ──────────────────────────────
        // Adjust "loggedInUserId" to match whatever key you store in session
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


//    @GetMapping("/Allevents")
//    public String Allevents(Model model, Authentication authentication) {
//
//        if (authentication == null) {
//            return "redirect:/login"; // prevent crash
//        }
//
//        String username = authentication.getName();
//        User organizer = userRepository.findByUsername(username).orElse(null);
//
//        if (organizer == null) {
//            model.addAttribute("error", "User not found");
//            return "error";
//        }
//        List<Event> events = eventService.getEventsByOrganizer(organizer);
//        model.addAttribute("events", events);
//
//        return "Organizer_AllEvent";
//    }
}



