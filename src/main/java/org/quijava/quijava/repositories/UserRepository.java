package org.quijava.quijava.repositories;


import org.quijava.quijava.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    boolean existsByUsername(String username);

    UserModel findByUsername(String username);
}