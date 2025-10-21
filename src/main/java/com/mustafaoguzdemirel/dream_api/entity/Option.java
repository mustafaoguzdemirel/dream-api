package com.mustafaoguzdemirel.dream_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "option_item") // 'option' SQL'de reserved keyword olduğu için güvenli ad
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public Option() {
    }

    public Option(Question question, String content) {
        this.question = question;
        this.content = content;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
