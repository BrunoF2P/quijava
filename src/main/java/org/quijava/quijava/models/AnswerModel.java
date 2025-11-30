package org.quijava.quijava.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @ElementCollection
    @CollectionTable(
            name = "answer_selected_options",
            joinColumns = @JoinColumn(name = "answer_id")
    )
    @Column(name = "option_id", nullable = false)
    @OrderColumn(name = "selection_order")
    private List<Integer> selectedAnswer = new ArrayList<>();

    @Column(name = "time_answer", nullable = false)
    private Duration timeAnswer;

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
        this.selectedAnswer = selectedAnswer != null ? selectedAnswer : new ArrayList<>();
    }

    public Duration getTimeAnswer() {
        return timeAnswer;
    }

    public void setTimeAnswer(Duration timeAnswer) {
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

    @PrePersist
    protected void onCreate() {
        if (this.dateCompleted == null) {
            this.dateCompleted = Instant.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerModel that = (AnswerModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AnswerModel{" +
                "id=" + id +
                ", winScore=" + winScore +
                ", timeAnswer=" + timeAnswer +
                ", dateCompleted=" + dateCompleted +
                '}';
    }
}
