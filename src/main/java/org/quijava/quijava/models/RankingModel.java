package org.quijava.quijava.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Duration;
import java.util.Date;

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

    @Column(name = "total_time", nullable = false)
    private Duration totalTime;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "date_completed")
    private Date dateCompleted;

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

    public Duration getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Duration totalTime) {
        this.totalTime = totalTime;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    @PrePersist
    protected void onCreate() {
        this.dateCompleted = new Date();
    }

}