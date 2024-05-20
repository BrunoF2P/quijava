package org.quijava.quijava.models;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class CategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_id_gen")
    @SequenceGenerator(name = "categories_id_gen", sequenceName = "categories_category_id_seq", allocationSize = 1)
    @Column(name = "category_id", nullable = false)
    private Integer id;

    @Column(name = "description", length = 64)
    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<QuizModel> quizzes = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<QuizModel> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Set<QuizModel> quizzes) {
        this.quizzes = quizzes;
    }

}