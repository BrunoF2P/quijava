package org.quijava.quijava.repositories;


import org.quijava.quijava.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    boolean existsByUsername(String username);

    UserModel findByUsername(String username);

    Optional<UserModel> findById(Integer integer);
}