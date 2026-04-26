package com.Java.EMS.controller;

import com.Java.EMS.entity.User;
import com.Java.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping({"/users"})
    public String userManagement(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "User_management";
    }


    @PostMapping("/users/create")
    public String createUser(
            @RequestParam(value = "fullName",   required = false) String fullName,
            @RequestParam(value = "email",      required = false) String email,
            @RequestParam(value = "role",       required = false) String role,
            @RequestParam(value = "phone",      required = false) String phone,
            @RequestParam(value = "department", required = false) String department,
            RedirectAttributes redirectAttributes
    ) {
        if (fullName == null || fullName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Full name is required.");
            return "redirect:/admin/users";
        }
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email is required.");
            return "redirect:/admin/users";
        }
        if (role == null || role.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Role is required.");
            return "redirect:/admin/users";
        }

        String error = userService.createUser(
                fullName.trim(), email.trim(), role.trim(),
                phone, department
        );

        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", error);
        }

        return "redirect:/admin/users";
    }


    @PostMapping("/users/authorize/{userId}")
    public String authorizeUser(@PathVariable String userId,
                                RedirectAttributes redirectAttributes) {
        String error = userService.authorizeUser(userId);
        if (error != null) redirectAttributes.addFlashAttribute("errorMessage", error);
        else redirectAttributes.addFlashAttribute("successMessage", "User authorized successfully.");
        return "redirect:/admin/users";
    }


    @PostMapping("/users/block/{userId}")
    public String blockUser(@PathVariable String userId,
                            RedirectAttributes redirectAttributes) {
        String error = userService.blockUser(userId);
        if (error != null) redirectAttributes.addFlashAttribute("errorMessage", error);
        else redirectAttributes.addFlashAttribute("successMessage", "User blocked successfully.");
        return "redirect:/admin/users";
    }


    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable String userId,
                             RedirectAttributes redirectAttributes) {
        String error = userService.deleteUser(userId);
        if (error != null) redirectAttributes.addFlashAttribute("errorMessage", error);
        else redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/admin/users";
    }
    @PostMapping("/users/edit/{userId}")
    public String editUser(
            @PathVariable String userId,
            @RequestParam(value = "fullName",   required = false) String fullName,
            @RequestParam(value = "email",      required = false) String email,
            @RequestParam(value = "phone",      required = false) String phone,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "role",       required = false) String role,
            @RequestParam(value = "status",     required = false) String status,
            RedirectAttributes redirectAttributes
    ) {
        String error = userService.editUser(userId, fullName, email, phone, department, role, status);
        if (error != null) redirectAttributes.addFlashAttribute("errorMessage", error);
        else redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        return "redirect:/admin/users";
    }
}
