package com.example.userServiceTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false,name = "name")
    private String name;

    @Column(unique = true, nullable = false, name = "surname")
    private String surname;

    @Past
    @Column(unique = true, nullable = false ,name = "birth_date")
    private LocalDate birthDate;

    @Email
    @Column(unique = true, nullable = false, name = "email")
    private String email;


    // IS IT GOOD? orphanRemoval = true. Strange thing
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardInfo> cards;
}
