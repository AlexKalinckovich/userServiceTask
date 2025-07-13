package com.example.userServiceTask.repositories.user;

import com.example.userServiceTask.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(long id);

    @Query(value = "SELECT u from User u where u.id IN :ids")
    List<User> findUserByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE User u SET u.name = :#{#user.name}, u.surname = :#{#user.surname}, " +
            "u.birthDate = :#{#user.birthDate}, u.email = :#{#user.email} WHERE u.id = :id")
    int updateUser(@Param("id") long id, @Param("user") User user);


    @Modifying
    @Query(value = "DELETE User u where u.id = :id")
    int deleteUserById(@Param("id") long id);


    @Query(
            value = "SELECT * from users u where u.email = :email",
            nativeQuery = true
    )
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);
}
