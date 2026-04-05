package com.Java.EMS.controller;

import com.Java.EMS.service.Student_DashboardService;
import com.Java.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private UserController userController;

    @Autowired
    private EventController eventController;

    @Autowired
    private Admin_DashboardController adminDashboardController;

    @Autowired
    private Student_DashboardController studentDashboardController;

    @GetMapping("/admin")
    public String sidebar() {
        return "/Fragments/Sidebar";
    }

}
