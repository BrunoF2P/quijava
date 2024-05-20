package org.quijava.quijava.models;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "type_question")
public class TypeQuestionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_question_id_gen")
    @SequenceGenerator(name = "type_question_id_gen", sequenceName = "type_question_type_question_id_seq", allocationSize = 1)
    @Column(name = "type_question_id", nullable = false)
    private Integer id;

    @Column(name = "type_question", nullable = false, length = 32)
    private String typeQuestion;

    @OneToMany(mappedBy = "typeQuestion")
    private Set<QuestionModel> questions = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(String typeQuestion) {
        this.typeQuestion = typeQuestion;
    }

    public Set<QuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionModel> questions) {
        this.questions = questions;
    }

}