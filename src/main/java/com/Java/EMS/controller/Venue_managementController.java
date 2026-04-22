package com.Java.EMS.controller;

import com.Java.EMS.entity.Venue;
import com.Java.EMS.service.Venue_managementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class Venue_managementController {
    private final Venue_managementService venueService;

    public Venue_managementController(Venue_managementService venueService) {
        this.venueService = venueService;
    }

    @GetMapping({"/venues"})
    public String venuePage(Model model) {
        model.addAttribute("venues",      venueService.getAllVenues());
        model.addAttribute("totalVenues", venueService.getTotalVenueCount());
        return "Venue_Management";
    }

    @PostMapping("/venues/add")
    public String addVenue(
            @RequestParam String vName,
            @RequestParam String location,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            Venue venue = new Venue(vName.trim(), description, location.trim());
            venueService.addVenue(venue);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Venue '" + vName + "' added successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/venues";
    }

    @PostMapping("/venues/{vName}/delete")
    public String deleteVenue(
            @PathVariable String vName,
            RedirectAttributes redirectAttributes) {
        try {
            venueService.deleteVenue(vName);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Venue '" + vName + "' deleted successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/venues";
    }
}
