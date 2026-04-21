package com.Java.EMS.repository;

//import com.Java.EMS.entity.User;
import com.Java.EMS.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, String>{

    Optional<Venue> findByVName(String vName);
}


