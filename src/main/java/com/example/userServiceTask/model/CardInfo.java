package com.example.userServiceTask.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "card_info")
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false, unique = true, name = "number", length = 16)
    private String number;


    @NotBlank
    @Column(nullable = false, unique = true, name = "holder")
    private String holder;


    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}")
    @Column(nullable = false, length = 5)
    private String expirationDate;
}
