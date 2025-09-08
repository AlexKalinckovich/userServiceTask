package com.example.userServiceTask.repositories.user;

import com.example.userServiceTask.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(long id);


    @Modifying
    @Query(value = "DELETE User u where u.id = :id")
    int deleteUserById(@Param("id") long id);


    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);
}
