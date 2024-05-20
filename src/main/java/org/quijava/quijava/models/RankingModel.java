package org.quijava.quijava.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "ranking")
public class RankingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ranking_id_gen")
    @SequenceGenerator(name = "ranking_id_gen", sequenceName = "ranking_ranking_id_seq", allocationSize = 1)
    @Column(name = "ranking_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizModel quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Lob
    @Column(name = "total_time", nullable = false)
    private String totalTime;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "date_completed")
    private Instant dateCompleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuizModel getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public Instant getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Instant dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

}