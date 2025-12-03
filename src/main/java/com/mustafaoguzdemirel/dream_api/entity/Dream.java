package com.mustafaoguzdemirel.dream_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dreams")
@Getter
@Setter
@NoArgsConstructor
public class Dream {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String dreamText;

    @Column(columnDefinition = "TEXT")
    private String interpretation;

    @Column(columnDefinition = "TEXT")
    private String detailedInterpretation;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Custom constructor
    public Dream(AppUser user, String dreamText, String interpretation) {
        this.user = user;
        this.dreamText = dreamText;
        this.interpretation = interpretation;
        this.createdAt = LocalDateTime.now();
    }
}
