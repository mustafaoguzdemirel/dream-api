package com.mustafaoguzdemirel.dream_api.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "email")
    private String email;

    @Column(name = "last_dream_interpreted_date")
    private LocalDate lastDreamInterpretedDate;

    public AppUser() {}

    public AppUser(UUID userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDate getLastDreamInterpretedDate() {
        return lastDreamInterpretedDate;
    }

    public void setLastDreamInterpretedDate(LocalDate lastDreamInterpretedDate) {
        this.lastDreamInterpretedDate = lastDreamInterpretedDate;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

