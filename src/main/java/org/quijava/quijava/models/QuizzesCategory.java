package org.quijava.quijava.models;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes_categories")
public class QuizzesCategory {
    @SequenceGenerator(name = "quizzes_categories_id_seq", sequenceName = "quizzes_quiz_id_seq", allocationSize = 1)
    @EmbeddedId
    private QuizzesCategoryId id;

    @MapsId("quiz_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizModel quiz;

    @MapsId("category_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;

    public QuizzesCategoryId getId() {
        return id;
    }

    public void setId(QuizzesCategoryId id) {
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