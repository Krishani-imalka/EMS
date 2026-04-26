package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.repository.EventRepository;
import com.Java.EMS.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/feedback/{eventId}")
    public String showFeedbackForm(@PathVariable Long eventId, Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUsername");
        if (username == null) {
            return "redirect:/login";
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        model.addAttribute("event", event);
        model.addAttribute("hasGivenFeedback", feedbackService.hasStudentGivenFeedback(eventId, username));

        return "Feedback_Form";
    }

    @PostMapping("/feedback/submit")
    public String submitFeedback(@RequestParam Long eventId,
                                 @RequestParam Integer rating,
                                 @RequestParam(required = false) String comment,
                                 HttpSession session) {
        String username = (String) session.getAttribute("loggedInUsername");
        if (username == null) {
            return "redirect:/login";
        }

        feedbackService.saveFeedback(eventId, username, rating, comment);

        return "redirect:/student/dashboard?feedbackSuccess=true";
    }
}
