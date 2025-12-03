package com.mustafaoguzdemirel.dream_api.entity;

import com.mustafaoguzdemirel.dream_api.dto.response.DreamListItemResponse;
import com.mustafaoguzdemirel.dream_api.entity.converter.DreamListItemResponseConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mood_analysis")
@Getter
@Setter
@NoArgsConstructor
public class MoodAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ElementCollection
    @CollectionTable(name = "mood_dominant_emotions", joinColumns = @JoinColumn(name = "mood_analysis_id"))
    @Column(name = "emotion")
    private List<String> dominantEmotions;

    @ElementCollection
    @CollectionTable(name = "mood_recurring_symbols", joinColumns = @JoinColumn(name = "mood_analysis_id"))
    @Column(name = "symbol")
    private List<String> recurringSymbols;

    @Column(columnDefinition = "TEXT")
    private String analysis;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Convert(converter = DreamListItemResponseConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<DreamListItemResponse> analyzedDreams;

    // Custom constructor
    public MoodAnalysis(AppUser user, List<String> dominantEmotions, List<String> recurringSymbols, String analysis) {
        this.user = user;
        this.dominantEmotions = dominantEmotions;
        this.recurringSymbols = recurringSymbols;
        this.analysis = analysis;
        this.createdAt = LocalDateTime.now();
    }
}

