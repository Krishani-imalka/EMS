package com.Java.EMS.controller;

import com.Java.EMS.entity.User;
import com.Java.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;


//
//    @GetMapping("/users")
//    public String userManagement() {
//        return "User_management";
//    }

    @GetMapping({"/users"})
    public String userManagement(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "User_management";
    }

}
