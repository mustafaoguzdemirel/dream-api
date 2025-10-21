package com.mustafaoguzdemirel.dream_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dreams")
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

    // --- Constructors ---
    public Dream() {}

    public Dream(AppUser user, String dreamText, String interpretation) {
        this.user = user;
        this.dreamText = dreamText;
        this.interpretation = interpretation;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getDreamText() {
        return dreamText;
    }

    public void setDreamText(String dreamText) {
        this.dreamText = dreamText;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }


    public String getDetailedInterpretation() {
        return detailedInterpretation;
    }

    public void setDetailedInterpretation(String detailedInterpretation) {
        this.detailedInterpretation = detailedInterpretation;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
