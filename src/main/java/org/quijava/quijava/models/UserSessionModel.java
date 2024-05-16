package org.quijava.quijava.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_session", schema = "public")
public class UserSessionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_id_seq")
    @SequenceGenerator(name = "session_id_seq", sequenceName = "session_id_seq", allocationSize = 1)
    private Integer id;
    private Integer userId;
    private String username;
    private Integer role;
    private LocalDateTime createTime;

    public void setSessionId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }


    public Integer getSessionId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Integer getRole() {
        return role;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }


}
