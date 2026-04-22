package com.Java.EMS.repository;

import com.Java.EMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByUserId(String userId);

    Optional<User> findByUsername(String username);

    List<User> findByStatusOrderByUserIdDesc(User.Status status);

    User findByEmail(String email);
    Optional<User> findTopByOrderByUserIdDesc();

    @Query("SELECT DISTINCT u.department FROM User u WHERE u.department IS NOT NULL ORDER BY u.department")
    List<String> findDistinctDepartments();
}
