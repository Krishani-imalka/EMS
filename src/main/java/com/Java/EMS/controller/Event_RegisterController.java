package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.entity.User;
import com.Java.EMS.service.Event_RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class Event_RegisterController {
    private final Event_RegisterService registrationService;

    public Event_RegisterController(Event_RegisterService registrationService) {
        this.registrationService = registrationService;
    }

    // Load registration page with approved events in dropdown
    @GetMapping("/register-page")
    public String showRegistrationPage(Model model, HttpSession session) {
        model.addAttribute("events", registrationService.getApprovedEvents());

        String username = (String) session.getAttribute("username");
        if (username != null) {
            User loggedInUser = registrationService.getLoggedInUser(username);
            model.addAttribute("loggedInUser", loggedInUser);
        }

        // Load distinct department values from the users table
        model.addAttribute("departments", registrationService.getDistinctDepartments());

        return "Event_Register";
    }

    // Handle registration form submission via AJAX
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerForEvent(
            @RequestParam Long eventId,
            @RequestParam String userId,
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam(required = false) String notes,
            HttpSession session) {
        try {
            String username = (String) session.getAttribute("username");

            if (username == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Session expired. Please log in again."
                ));
            }

            Event_Registation saved = registrationService.registerStudentForEvent(eventId, userId, username);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "registrationId", saved.getRegistrationId(),
                    "message", "You have successfully registered for the event!"
            ));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }
}
