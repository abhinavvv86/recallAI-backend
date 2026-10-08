package com.recallai.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public QuizResult() {
    }

    public QuizResult(
            int totalQuestions,
            int correctAnswers,
            String subject,
            User user
    ) {
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score =
                totalQuestions > 0
                        ? ((double) correctAnswers / totalQuestions) * 100
                        : 0.0;
        this.subject = subject;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}