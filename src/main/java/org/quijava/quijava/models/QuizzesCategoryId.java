package org.quijava.quijava.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuizzesCategoryId implements Serializable {
    private static final long serialVersionUID = -799806918591193423L;
    @Column(name = "quiz_id", nullable = false)
    private Integer quiz_id;

    @Column(name = "category_id", nullable = false)
    private Integer category_id;

    public Integer getQuizId() {
        return quiz_id;
    }

    public void setQuizId(Integer quiz_id) {
        this.quiz_id = quiz_id;
    }

    public Integer getCategoryId() {
        return category_id;
    }

    public void setCategoryId(Integer category_id) {
        this.category_id = category_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        QuizzesCategoryId entity = (QuizzesCategoryId) o;
        return Objects.equals(this.quiz_id, entity.quiz_id) &&
                Objects.equals(this.category_id, entity.category_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quiz_id, category_id);
    }

}