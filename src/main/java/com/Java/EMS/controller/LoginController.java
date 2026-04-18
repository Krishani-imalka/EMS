package com.Java.EMS.controller;

import com.Java.EMS.entity.User;
import com.Java.EMS.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // ── GET: show the login page ──────────────────────────────────────────────
    @GetMapping({"/", "/login"})
    public String showLogin() {
        return "/Fragments/Login_page";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String userid,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session,
            Model model
    ) {

        Optional<User> optionalUser = userRepository.findByUserId(userid.trim());

        if(optionalUser.isPresent()) {

            User user = optionalUser.get();

            boolean passwordMatch = user.getPssaword().equals(password);
            boolean roleMatch     = user.getRole().name().equalsIgnoreCase(role);
            boolean isActive      = user.getStatus() == User.Status.ACTIVE;

            if (passwordMatch && roleMatch && isActive) {
                // ── Save user info in session ──────────────────────────────
                session.setAttribute("loggedInUser", user);
                session.setAttribute("loggedInUserId",   user.getUserId());
                session.setAttribute("loggedInUsername", user.getUsername());
                session.setAttribute("loggedInRole",     user.getRole().name());
                session.setAttribute("loggedInName",     user.getFullName());

                // ── Redirect by role ───────────────────────────────────────
                switch (user.getRole()) {
                    case ADMIN:     return "redirect:/admin";
                    case ORGANIZER: return "redirect:/organizer";
                    case STUDENT:   return "redirect:/student";
                }
            }

            // Give specific error messages
            if (!passwordMatch) {
                model.addAttribute("error", "Incorrect password.");
            } else if (!roleMatch) {
                model.addAttribute("error", "Selected role does not match your account.");
            } else if (!isActive) {
                model.addAttribute("error", "Your account is not active yet. Please contact admin.");
            }

        } else {
            model.addAttribute("error", "No account found with User ID: " + userid);
        }

        return "/Fragments/Login_page";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}