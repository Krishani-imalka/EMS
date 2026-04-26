package com.Java.EMS.service;

import com.Java.EMS.entity.Venue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Venue_managementService {
    private final VenueRepository venueRepository;

    public Venue_managementService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public long getTotalVenueCount() {
        return venueRepository.count();
    }

    public void addVenue(Venue venue) {
        if (venueRepository.existsById(venue.getvName())) {
            throw new IllegalArgumentException(
                    "A venue with the name '" + venue.getvName() + "' already exists.");
        }
        venueRepository.save(venue);
    }

    public void deleteVenue(String vName) {
        if (!venueRepository.existsById(vName)) {
            throw new IllegalArgumentException("Venue not found: " + vName);
        }
        venueRepository.deleteById(vName);
    }
}
