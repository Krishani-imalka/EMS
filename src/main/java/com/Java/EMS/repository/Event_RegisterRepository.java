package com.Java.EMS.repository;

import com.Java.EMS.entity.Event;
import com.Java.EMS.entity.Event_Registation;
import com.Java.EMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Event_RegisterRepository extends JpaRepository <Event_Registation , Long>{
    boolean existsByEventAndStudent(Event event, User student);
    List<Event_Registation> findTop10ByOrderByRegisteredAtDesc();

    List<Event_Registation> findTop10ByRegistrationStatusOrderByRegisteredAtDesc(
            Event_Registation.RegistrationStatus status);

    List<Event_Registation> findByStudentOrderByRegisteredAtDesc(User student);

    long countByEventAndRegistrationStatusNot(Event event,Event_Registation.RegistrationStatus status);
    long countByEventAndRegistrationStatus(Event event, Event_Registation.RegistrationStatus status);


    List<Event_Registation> findByEventOrderByRegisteredAtDesc(Event event);
    List<Event_Registation> findByEvent_OrganizerOrderByRegisteredAtDesc(User organizer);
}


