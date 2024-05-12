package org.quijava.quijava.repositories;

import org.quijava.quijava.models.UserSessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionModel, Integer> {

    boolean existsById(Integer id);

    @Override
    Optional<UserSessionModel> findById(Integer integer);

    @Override
    void deleteById(Integer integer);


    @Query(value = "SELECT MAX(id) FROM UserSessionModel")
    Integer getLastSessionId();

    @Query("SELECT id FROM UserSessionModel WHERE username = :username")
    Optional<Integer> getSessionIdForUser(String username);

}
