package com.Java.EMS.service;

import com.Java.EMS.entity.User;
import com.Java.EMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public String createUser(String fullName, String email, String role,
                             String phone, String department) {

        // Validate role value
        User.Role userRole;
        try {
            userRole = User.Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Invalid role: " + role;
        }

        // Check duplicate email
        if (userRepository.findByEmail(email) != null) {
            return "A user with this email already exists.";
        }

        // Generate unique userId
        String lastId = userRepository.findTopByOrderByUserIdDesc()
                .map(User::getUserId)
                .orElse(null);

        int nextNumber = 1;

        if (lastId != null) {
            try {
                nextNumber = Integer.parseInt(lastId.substring(1)) + 1;
            } catch (Exception e) {
                return "Invalid existing User ID format.";
            }
        }

        String userId = "U" + String.format("%03d", nextNumber);


        // Generate unique username from fullName
        String baseUsername = fullName.toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z0-9.]", "");

        if (baseUsername.isBlank()) baseUsername = "user";

        String username = baseUsername;
        int suffix = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + suffix++;
        }

        // Build and save user
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPssaword("password123");
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(userRole);
        user.setStatus(User.Status.PENDING);
        user.setPhone(phone  != null && !phone.isBlank()      ? phone.trim()      : null);
        user.setDepartment(department != null && !department.isBlank() ? department.trim() : null);

        userRepository.save(user);
        return null;
    }

    public String authorizeUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "User not found.";
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
        return null;
    }

    public String blockUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "User not found.";
        user.setStatus(User.Status.BLOCKED);
        userRepository.save(user);
        return null;
    }

    public String deleteUser(String userId) {
        if (!userRepository.existsById(userId)) return "User not found.";
        userRepository.deleteById(userId);
        return null;
    }

    public String editUser(String userId, String fullName, String email,
                           String phone, String department, String role, String status) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "User not found.";

        // Check email not taken by another user
        User existing = userRepository.findByEmail(email);
        if (existing != null && !existing.getUserId().equals(userId)) {
            return "Email already in use by another user.";
        }

        try { user.setRole(User.Role.valueOf(role.toUpperCase())); }
        catch (IllegalArgumentException e) { return "Invalid role: " + role; }

        try { user.setStatus(User.Status.valueOf(status.toUpperCase())); }
        catch (IllegalArgumentException e) { return "Invalid status: " + status; }

        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhone(phone != null && !phone.isBlank()           ? phone.trim()      : null);
        user.setDepartment(department != null && !department.isBlank() ? department.trim() : null);

        userRepository.save(user);
        return null;
    }
}

