package com.Java.EMS.controller;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.service.ParticipantsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/organizer")
public class ParticipantsController {
    private final ParticipantsService participantService;

    public ParticipantsController(ParticipantsService participantsService) {
        this.participantService = participantsService;
    }

    @GetMapping("/participants")
    public String viewParticipants(
            @RequestParam(required = false) Long eventId,
            Model model,
            HttpSession session) {

        String username = (String) session.getAttribute("loggedInUsername");

        if (username == null) {
            return "redirect:/login";
        }

        // Load organizer's events for the filter dropdown
        List<Event> organizerEvents = participantService.getOrganizerEvents(username);
        model.addAttribute("organizerEvents", organizerEvents);
        model.addAttribute("selectedEventId", eventId);

        // Load participants — filtered by event or all
        List<Event_Registation> participants;
        if (eventId != null) {
            participants = participantService.getParticipantsByEvent(eventId);
        } else {
            participants = participantService.getAllParticipants(username);
        }

        model.addAttribute("participants", participants);

        // Stats
        model.addAttribute("totalCount",      participants.size());
        model.addAttribute("registeredCount", participantService.countRegistered(participants));
        model.addAttribute("pendingCount",    participantService.countPending(participants));
        model.addAttribute("cancelledCount",  participantService.countCancelled(participants));

        return "Participants";
    }
}
