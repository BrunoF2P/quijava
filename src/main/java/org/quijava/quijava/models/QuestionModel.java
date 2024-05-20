package org.quijava.quijava.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_question_id", nullable = false)
    private TypeQuestionModel typeQuestion;

    @Column(name = "question_text", nullable = false, length = Integer.MAX_VALUE)
    private String questionText;

    @ColumnDefault("0")
    @Column(name = "total_attempts", nullable = false)
    private Integer totalAttempts;

    @Lob
    @Column(name = "limite_time", nullable = false)
    private String limiteTime;

    @Column(name = "image_question")
    private byte[] imageQuestion;

    @ColumnDefault("0")
    @Column(name = "correct_attempts", nullable = false)
    private Integer correctAttempts;

    @OneToMany(mappedBy = "question")
    private Set<AnswerModel> answers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question")
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

    public TypeQuestionModel getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(TypeQuestionModel typeQuestion) {
        this.typeQuestion = typeQuestion;
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

    public String getLimiteTime() {
        return limiteTime;
    }

    public void setLimiteTime(String limiteTime) {
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

}