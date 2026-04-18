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

    // ── GET: show the login page
    @GetMapping({ "/login"})
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

    // ── Step 1: Show forgot password form ────────────────────────────────────
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "Fragments/Forgot_password";
    }

    // ── Step 2: Verify userId + email ─────────────────────────────────────────
    @PostMapping("/forgot-password/verify")
    public String verifyIdentity(
            @RequestParam String userid,
            @RequestParam String email,
            HttpSession session,
            Model model
    ) {
        Optional<User> optionalUser = userRepository.findById(userid.trim());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equalsIgnoreCase(email.trim())) {
                session.setAttribute("resetUserId", user.getUserId());
                return "redirect:/forgot-password/reset";
            }
            model.addAttribute("error", "Email does not match our records for this User ID.");
        } else {
            model.addAttribute("error", "No account found with that User ID.");
        }

        return "Fragments/Forgot_password";
    }

    // ── Step 3: Show reset form ───────────────────────────────────────────────
    @GetMapping("/forgot-password/reset")
    public String showResetForm(HttpSession session) {
        if (session.getAttribute("resetUserId") == null) {
            return "redirect:/forgot-password";
        }
        return "Fragments/Reset_password";
    }

    // ── Step 4: Save new password to DB ──────────────────────────────────────
    @PostMapping("/forgot-password/reset")
    public String resetPassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model
    ) {
        String resetUserId = (String) session.getAttribute("resetUserId");

        if (resetUserId == null) {
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "Fragments/Reset_password";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "Fragments/Reset_password";
        }

        // ✅ Find user and update password column in DB
        Optional<User> optionalUser = userRepository.findById(resetUserId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPssaword(newPassword);       // updates "password" column via setPssaword()
            userRepository.save(user);           // saves to DB
        }

        session.removeAttribute("resetUserId");
        model.addAttribute("success", "Password reset successfully. Please log in.");
        return "Fragments/Login_page";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}