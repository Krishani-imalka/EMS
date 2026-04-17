package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;
import com.Java.EMS.entity.Venue;
import com.Java.EMS.service.EventService;
import com.Java.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping({"/events"})
    public String eventManagement(Model model) {
        List<Event> events = eventService.getAllEvents();
        List<Venue> venues = eventService.getAllVenues();
        model.addAttribute("events", events);
        model.addAttribute("venues", venues);
        return "Event_management";
    }

    // ── CREATE (inline panel form POST) ──────────────────────────────────────
    @PostMapping("/events/create")
    public String createEvent(
            @RequestParam String organizerUserId,
            @RequestParam String eventName,
            @RequestParam String description,
            @RequestParam String eventDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String location,
            @RequestParam(required = false) String venueName,
            @RequestParam String category,
            @RequestParam Integer expectedAttendees,
            @RequestParam String contactInfo,
            @RequestParam(required = false) MultipartFile bannerImage,
            RedirectAttributes ra) {

        String error = eventService.createEvent(
                organizerUserId,
                eventName,
                description,
                LocalDate.parse(eventDate),
                LocalTime.parse(startTime),
                LocalTime.parse(endTime),
                location,
                venueName,
                Event.EventCategory.valueOf(category),
                expectedAttendees,
                contactInfo,
                bannerImage
        );

        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
        } else {
            ra.addFlashAttribute("successMsg", "Event '" + eventName + "' created successfully.");
        }
        return "redirect:/admin/events";
    }

    // ── VIEW single event (returns JSON for modal) ───────────────────────────
    @GetMapping("/events/{id}")
    @ResponseBody
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("eventId",           event.getEventId());
        data.put("eventName",         event.getEventName());
        data.put("organizer",         event.getOrganizer().getFullName());
        data.put("description",       event.getDescription());
        data.put("eventDate",         event.getEventDate().toString());
        data.put("startTime",         event.getStartTime().toString());
        data.put("endTime",           event.getEndTime().toString());
        data.put("location",          event.getLocation());
        // Use the correct getter from your Venue entity (vName field -> getVName())
        data.put("venue",             event.getVenue() != null ? event.getVenue().getvName() : "N/A");
        data.put("category",          event.getCategory().name());
        data.put("expectedAttendees", event.getExpectedAttendees());
        data.put("contactInfo",       event.getContactInfo());
        data.put("status",            event.getStatus().name());
        data.put("adminRemarks",      event.getAdminRemarks() != null ? event.getAdminRemarks() : "");
        data.put("bannerImage",       event.getBannerImage() != null ? event.getBannerImage() : "");
        data.put("createdAt",         event.getCreatedAt().toString());

        return ResponseEntity.ok(data);
    }

    // ── APPROVE ──────────────────────────────────────────────────────────────
    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Long id,
                               @RequestParam(required = false) String adminRemarks,
                               RedirectAttributes ra) {
        String error = eventService.updateEventStatus(id, Event.EventStatus.APPROVED, adminRemarks);
        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
        } else {
            ra.addFlashAttribute("successMsg", "Event approved successfully.");
        }
        return "redirect:/admin/events";
    }

    // ── REJECT ───────────────────────────────────────────────────────────────
    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id,
                              @RequestParam(required = false) String adminRemarks,
                              RedirectAttributes ra) {
        String error = eventService.updateEventStatus(id, Event.EventStatus.REJECTED, adminRemarks);
        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
        } else {
            ra.addFlashAttribute("successMsg", "Event rejected.");
        }
        return "redirect:/admin/events";
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes ra) {
        String error = eventService.deleteEvent(id);
        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
        } else {
            ra.addFlashAttribute("successMsg", "Event deleted successfully.");
        }
        return "redirect:/admin/events";
    }

    // ── EDIT PAGE ────────────────────────────────────────────────────────────
    @GetMapping("/events/{id}/edit")
    public String editEventPage(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return "redirect:/admin/events";
        }
        List<Venue> venues = eventService.getAllVenues();
        model.addAttribute("event", event);
        model.addAttribute("venues", venues);
        model.addAttribute("categories", Event.EventCategory.values());
        return "Event_edit";          // create this Thymeleaf template (see below)
    }

    // ── EDIT SUBMIT ──────────────────────────────────────────────────────────
    @PostMapping("/events/{id}/edit")
    public String updateEvent(
            @PathVariable Long id,
            @RequestParam String eventName,
            @RequestParam String description,
            @RequestParam String eventDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String location,
            @RequestParam(required = false) String venueName,
            @RequestParam String category,
            @RequestParam Integer expectedAttendees,
            @RequestParam String contactInfo,
            @RequestParam(required = false) MultipartFile bannerImage,
            RedirectAttributes ra) {

        String error = eventService.updateEvent(
                id,
                eventName,
                description,
                LocalDate.parse(eventDate),
                LocalTime.parse(startTime),
                LocalTime.parse(endTime),
                location,
                venueName,
                Event.EventCategory.valueOf(category),
                expectedAttendees,
                contactInfo,
                bannerImage
        );

        if (error != null) {
            ra.addFlashAttribute("errorMsg", error);
            return "redirect:/admin/events/" + id + "/edit";
        }
        ra.addFlashAttribute("successMsg", "Event updated successfully.");
        return "redirect:/admin/events";
    }
}
