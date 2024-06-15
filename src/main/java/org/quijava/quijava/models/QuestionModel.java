package org.quijava.quijava.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "questions")
public class QuestionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questions_id_gen")
    @SequenceGenerator(name = "questions_id_gen", sequenceName = "questions_question_id_seq", allocationSize = 1)
    @Column(name = "question_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizModel quiz;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type_question", nullable = false)
    private TypeQuestion typeQuestion;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "question_difficulty", nullable = false)
    private QuestionDifficulty questionDifficulty;

    @Column(name = "question_text", nullable = false, length = Integer.MAX_VALUE)
    private String questionText;

    @ColumnDefault("0")
    @Column(name = "total_attempts", nullable = false)
    private Integer totalAttempts;


    @Column(name = "limite_time", nullable = false)
    private Duration limiteTime;

    @Lob
    @Column(name = "image_question")
    private byte[] imageQuestion;

    @ColumnDefault("0")
    @Column(name = "correct_attempts", nullable = false)
    private Integer correctAttempts;

    @OneToMany(mappedBy = "question")
    private Set<AnswerModel> answers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<OptionsAnswerModel> optionsAnswers = new LinkedHashSet<>();

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

    public TypeQuestion getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(TypeQuestion typeQuestion) {
        this.typeQuestion = typeQuestion;
    }

    public QuestionDifficulty getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(QuestionDifficulty questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Duration getLimiteTime() {
        return limiteTime;
    }

    public void setLimiteTime(Duration limiteTime) {
        this.limiteTime = limiteTime;
    }

    public byte[] getImageQuestion() {
        return imageQuestion;
    }

    public void setImageQuestion(byte[] imageQuestion) {
        this.imageQuestion = imageQuestion;
    }

    public Integer getCorrectAttempts() {
        return correctAttempts;
    }

    public void setCorrectAttempts(Integer correctAttempts) {
        this.correctAttempts = correctAttempts;
    }

    public Set<AnswerModel> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<AnswerModel> answers) {
        this.answers = answers;
    }

    public Set<OptionsAnswerModel> getOptionsAnswers() {
        return optionsAnswers;
    }

    public void setOptionsAnswers(Set<OptionsAnswerModel> optionsAnswers) {
        this.optionsAnswers = optionsAnswers;
    }

    @PrePersist
    protected void onCreate() {
        this.totalAttempts = 0;
        this.correctAttempts = 0;
    }

}