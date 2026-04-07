package com.Java.EMS.service;

import com.Java.EMS.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Organizer_DashboardService {

    @Autowired
    private EventRepository eventRepository;
}
