package com.Java.EMS.service;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.User;

import java.util.List;

public interface Admin_DashboardService {

    long getTotalUsers();

    long getTotalEvents();

    long getActiveEventCount();

    long getPendingApprovalCount();

    List<Event> getPendingEvents();

    List<User> getPendingUsers();

    void activeUser(String userId);

     void approvedEvent(Long eventId);

}