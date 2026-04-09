package com.Java.EMS.controller;

import com.Java.EMS.service.Admin_DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class Admin_DashboardController {

    @Autowired
    private Admin_DashboardService adminDashboardService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("totalUsers",adminDashboardService.getTotalUsers());
        model.addAttribute("totalEvents",adminDashboardService.getTotalEvents());
        model.addAttribute("activeEvents",adminDashboardService.getActiveEventCount());
        model.addAttribute("pendingApprovals",adminDashboardService.getPendingApprovalCount());

        model.addAttribute("pendingEvents",adminDashboardService.getPendingEvents());
        model.addAttribute("pendingUsers",adminDashboardService.getPendingUsers());

        return "Admin_Dashboard";
    }

    @PostMapping("/users/{userId}/active")
    public String activeUser(@PathVariable String userId) {
        adminDashboardService.activeUser(userId);
        return "Admin_Dashboard";
    }

    @PostMapping("/events/{eventId}/approved")
    public String approvedEvent(@PathVariable long eventId) {
        adminDashboardService.approvedEvent(eventId);
        return "Admin_Dashboard";
    }

}