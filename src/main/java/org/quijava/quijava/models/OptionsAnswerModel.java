package org.quijava.quijava.models;

import jakarta.persistence.*;

@Entity
@Table(name = "options_answer")
public class OptionsAnswerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "options_answer_id_gen")
    @SequenceGenerator(name = "options_answer_id_gen", sequenceName = "options_answer_option_answer_id_seq", allocationSize = 1)
    @Column(name = "option_answer_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionModel question;

    @Column(name = "option_text", nullable = false, length = Integer.MAX_VALUE)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "score", nullable = false)
    private Integer score;

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

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

}