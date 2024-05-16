package org.quijava.quijava.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "quizzes")
public class QuizModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quizzes_id_seq")
    @SequenceGenerator(name = "quizzes_id_seq", sequenceName = "quizzes_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "author_id", nullable = false)
    private Integer author_id;

    private Date created_at;

    private Date updated_at;

    @Lob
    @Column(name = "image_quiz")
    private byte[] image_quiz;

    @ManyToMany(mappedBy = "quizzes")
    private Set<CategoryModel> categories = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAuthorId() {
        return author_id;
    }

    public void setAuthorId(Integer author_id) {
        this.author_id = author_id;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updated_at = updated_at;
    }

    public byte[] getImageQuiz() {
        return image_quiz;
    }

    public void setImageQuiz(byte[] imageQuiz) {
        this.image_quiz = imageQuiz;
    }

    public Set<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryModel> categories) {
        this.categories = categories;
    }

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
        this.updated_at = new Date();
    }

    @PreUpdate
    protected void updateDate(){
        this.updated_at = new Date();
    }

}