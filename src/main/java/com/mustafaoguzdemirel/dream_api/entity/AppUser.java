package com.mustafaoguzdemirel.dream_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

    @Id
    private UUID id;  // Manuel olarak set edilecek (UUID.randomUUID())

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "email")
    private String email;

    @Column(name = "last_dream_interpreted_date")
    private LocalDate lastDreamInterpretedDate;

    // Custom constructor - UUID ile user oluşturmak için
    public AppUser(UUID id) {
        this.id = id;
    }

    /**
     * Backward compatibility için getUserId metodu
     * Artık id ve userId aynı şey
     * Lombok @Getter ile getId() otomatik oluşturuluyor
     */
    public UUID getUserId() {
        return id;
    }

    public void setUserId(UUID userId) {
        this.id = userId;
    }
}

