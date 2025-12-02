package com.mustafaoguzdemirel.dream_api.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    private UUID id;  // Manuel olarak set edilecek (UUID.randomUUID())

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "email")
    private String email;

    @Column(name = "last_dream_interpreted_date")
    private LocalDate lastDreamInterpretedDate;

    public AppUser() {}

    public AppUser(UUID id) {
        this.id = id;
    }

    /**
     * Primary key: UUID
     * Artık userId yerine direkt id kullanıyoruz
     */
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Backward compatibility için getUserId metodu
     * Artık id ve userId aynı şey
     */
    public UUID getUserId() {
        return id;
    }

    public void setUserId(UUID userId) {
        this.id = userId;
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

