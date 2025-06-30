package com.example.userServiceTask.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "card_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String number;

    @NotBlank
    @Column(nullable = false, length = 25)
    private String holder;

    @Future(message = "Must be future date")
    @NotNull
    private LocalDate expirationDate;
}
