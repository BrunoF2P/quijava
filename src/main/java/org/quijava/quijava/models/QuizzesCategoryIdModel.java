package org.quijava.quijava.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuizzesCategoryIdModel implements Serializable {
    private static final long serialVersionUID = 8862779137220059823L;
    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        QuizzesCategoryIdModel entity = (QuizzesCategoryIdModel) o;
        return Objects.equals(this.quizId, entity.quizId) &&
                Objects.equals(this.categoryId, entity.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, categoryId);
    }

}