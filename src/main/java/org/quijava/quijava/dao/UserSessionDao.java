package org.quijava.quijava.dao;

import org.jetbrains.annotations.NotNull;
import org.quijava.quijava.models.UserSessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionDao extends JpaRepository<@NotNull UserSessionModel, @NotNull Integer> {
    Optional<UserSessionModel> findByUsername(String username);

    @Query("SELECT MAX(u.id) FROM UserSessionModel u WHERE u.username = :username")
    Integer getLastSessionIdForUser(@Param("username") String username);

    void deleteByUsername(String username);
}
