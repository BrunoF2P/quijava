package org.quijava.quijava.models;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes_categories")
public class QuizzesCategoryModel {
    @SequenceGenerator(name = "quizzes_categories_id_gen", sequenceName = "quizzes_categories_id_seq", allocationSize = 1)
    @EmbeddedId
    private QuizzesCategoryIdModel id;

    @MapsId("quizId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizModel quiz;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;

    public QuizzesCategoryIdModel getId() {
        return id;
    }

    public void setId(QuizzesCategoryIdModel id) {
        this.id = id;
    }

    public QuizModel getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

}