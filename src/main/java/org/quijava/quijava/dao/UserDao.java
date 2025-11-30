package org.quijava.quijava.dao;

import org.jetbrains.annotations.NotNull;
import org.quijava.quijava.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<@NotNull UserModel, @NotNull Integer> {

    Optional<UserModel> findByUsername(String username);

    boolean existsByUsername(String username);
}
