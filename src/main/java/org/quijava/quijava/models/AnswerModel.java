package org.quijava.quijava.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "answers")
public class AnswerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answers_id_gen")
    @SequenceGenerator(name = "answers_id_gen", sequenceName = "answers_answers_id_seq", allocationSize = 1)
    @Column(name = "answers_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionModel question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "selected_answer", nullable = false)
    private List<Integer> selectedAnswer;

    @Lob
    @Column(name = "time_answer", nullable = false)
    private String timeAnswer;

    @Column(name = "win_score", nullable = false)
    private Integer winScore;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "date_completed")
    private Instant dateCompleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionModel getQuestion() {
        return question;
    }

    public void setQuestion(QuestionModel question) {
        this.question = question;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public List<Integer> getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(List<Integer> selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getTimeAnswer() {
        return timeAnswer;
    }

    public void setTimeAnswer(String timeAnswer) {
        this.timeAnswer = timeAnswer;
    }

    public Integer getWinScore() {
        return winScore;
    }

    public void setWinScore(Integer winScore) {
        this.winScore = winScore;
    }

    public Instant getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Instant dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

}