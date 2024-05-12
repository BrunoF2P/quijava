package org.quijava.quijava.models;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "categories", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "unique_description", columnNames = {"description"})
})
@org.hibernate.annotations.DynamicInsert
public class CategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_id_seq")
    @SequenceGenerator(name = "categories_id_seq", sequenceName = "categories_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "description", length = 64)
    private String description;



    @ManyToMany
    @JoinTable(name = "quizzes_categories",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id"))
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