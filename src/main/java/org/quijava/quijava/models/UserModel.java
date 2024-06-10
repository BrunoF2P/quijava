package org.quijava.quijava.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@org.hibernate.annotations.DynamicInsert
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @ColumnDefault("1")
    @Column(name = "role", nullable = false)
    private Integer role;

    @OneToMany(mappedBy = "user")
    private Set<AnswerModel> answers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<QuizModel> quizzes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<RankingModel> rankings = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Set<AnswerModel> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<AnswerModel> answers) {
        this.answers = answers;
    }

    public Set<QuizModel> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Set<QuizModel> quizzes) {
        this.quizzes = quizzes;
    }

    public Set<RankingModel> getRankings() {
        return rankings;
    }

    public void setRankings(Set<RankingModel> rankings) {
        this.rankings = rankings;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void updateDate(){
        this.updatedAt = new Date();
    }

}