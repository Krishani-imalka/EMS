package com.Java.EMS.controller;

import com.Java.EMS.entity.User;
import com.Java.EMS.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ── Show profile — loads the LOGGED IN user from session ─────────────────
    @GetMapping({"/admin/profile", "/organizer/profile", "/student/profile"})
    public String showProfile(HttpSession session, Model model) {

        String userId = (String) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return "redirect:/login";

        User user = optionalUser.get();

        // ✅ Pass only safe fields — no password, no internal IDs
        model.addAttribute("userName",    user.getUsername());
        model.addAttribute("fullName",    user.getFullName());
        model.addAttribute("email",       user.getEmail());
        model.addAttribute("phone",       user.getPhone());
        model.addAttribute("department",  user.getDepartment());
        model.addAttribute("role",        user.getRole().name());
        model.addAttribute("status",      user.getStatus().name());
        model.addAttribute("profileImage",user.getProfileImage());

        return "Fragments/Profile";
    }

    // ── Upload profile photo ──────────────────────────────────────────────────
    @PostMapping("/profile/upload-photo")
    public String uploadPhoto(
            @RequestParam("photo") MultipartFile photo,
            HttpSession session
    ) {
        String userId = (String) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) return "redirect:/login";

        User user = optionalUser.get();

        try {
            String uploadDir = "src/main/resources/static/uploads/profiles/";
            Files.createDirectories(Paths.get(uploadDir));

            String original = photo.getOriginalFilename();
            String ext      = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : ".jpg";
            String filename = userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

            Files.write(Paths.get(uploadDir + filename), photo.getBytes());

            user.setProfileImage(filename);
            userRepository.save(user);

            // ✅ Refresh session so header shows new photo immediately
            session.setAttribute("loggedInUser", user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String role = (String) session.getAttribute("loggedInRole");
        return switch (role) {
            case "ADMIN"     -> "redirect:/admin/profile";
            case "ORGANIZER" -> "redirect:/organizer/profile";
            default          -> "redirect:/student/profile";
        };
    }
}