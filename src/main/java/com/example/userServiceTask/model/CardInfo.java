package com.example.userServiceTask.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    @Size(min = 16, max = 16)
    @Column(nullable = false, unique = true)
    private String number;

    @NotBlank
    @Column(nullable = false)
    private String holder;

    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}")
    @Column(name = "expiration_date", nullable = false, length = 5)
    private String expirationDate;
}
