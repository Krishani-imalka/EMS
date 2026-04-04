package com.Java.EMS.repository;

import com.Java.EMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Admin_DashboardRepository extends JpaRepository<User, String> {

//    long countByRole(User.Role role);
//    List<User> findByStatus(User.Status status);
}