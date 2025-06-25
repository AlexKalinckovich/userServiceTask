package com.example.userServiceTask.repositories;

import com.example.userServiceTask.model.User;
import com.example.userServiceTask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findById(long id);

    @Query(value = "SELECT u from User u where u.id IN :ids")
    List<User> findUserByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "UPDATE User u SET u = :user where u.id = :id")
    User updateUserById(@Param("id") long id,@Param("user") User user);


    @Modifying
    @Query(value = "DELETE User u where u.id = :id")
    User deleteUserById(@Param("id") long id);


    @Query(
            value = "SELECT * from users u where u.email = :email",
            nativeQuery = true
    )
    User findByEmail(@Param(":email") String email);
}
