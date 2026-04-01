package com.Java.EMS.entity;

import ch.qos.logback.core.status.Status;
import jakarta.persistence.*;

import javax.management.relation.Role;

@Entity
@Table(name = "users")

public class User {
    @Id
    @Column(name = "user_id", length = 10, nullable = false, unique = true)
    private String userId;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String pssaword;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;


    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    public User() {
    }

    public User(String userId, String username, String pssaword, String email, String fullName, Role role, Status status, String phone, String department, String profileImage) {
        this.userId = userId;
        this.username = username;
        this.pssaword = pssaword;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.phone = phone;
        this.department = department;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPssaword() {
        return pssaword;
    }

    public void setPssaword(String pssaword) {
        this.pssaword = pssaword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    enum Role {
        ADMIN, ORGANIZER, STUDENT
    }


    enum Status {
        ACTIVE, PENDING, BLOCKED
    }

}

