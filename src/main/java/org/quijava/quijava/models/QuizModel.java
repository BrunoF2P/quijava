package org.quijava.quijava.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "quizzes")
@org.hibernate.annotations.DynamicInsert
public class QuizModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quizzes_id_gen")
    @SequenceGenerator(name = "quizzes_id_gen", sequenceName = "quizzes_quiz_id_seq", allocationSize = 1)
    @Column(name = "quiz_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserModel author;

    @Column(name = "total_attempts", nullable = false)
    private Integer totalAttempts;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Lob
    @Column(name = "image_quiz")
    private byte[] imageQuiz;

    @OneToMany(mappedBy = "quiz")
    private Set<QuestionModel> questions = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "quizzes_categories",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryModel> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "quiz")
    private Set<RankingModel> rankings = new LinkedHashSet<>();

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

    public UserModel getAuthor() {
        return author;
    }

    public void setAuthor(UserModel author) {
        this.author = author;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public byte[] getImageQuiz() {
        return imageQuiz;
    }

    public void setImageQuiz(byte[] imageQuiz) {
        this.imageQuiz = imageQuiz;
    }

    public Set<QuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionModel> questions) {
        this.questions = questions;
    }

    public Set<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryModel> categories) {
        this.categories = categories;
    }

    public Set<RankingModel> getRankings() {
        return rankings;
    }

    public void setRankings(Set<RankingModel> rankings) {
        this.rankings = rankings;
    }

    public void addCategory(CategoryModel category) {
        categories.add(category);
        category.getQuizzes().add(this);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.totalAttempts = 0;
    }

    @PreUpdate
    protected void updateDate(){
        this.updatedAt = new Date();
    }

}