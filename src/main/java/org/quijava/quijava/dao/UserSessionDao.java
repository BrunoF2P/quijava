package org.quijava.quijava.dao;

import org.quijava.quijava.models.UserSessionModel;

import java.util.Optional;

public interface UserSessionDao {
    boolean existsById(Integer id);

    boolean delete(Integer id);

    Optional<UserSessionModel> findById(Integer id);

    void deleteById(Integer id);

    Integer getLastSessionIdForUser(String username);

    Optional<Integer> getSessionIdForUser(String username);

    void save(UserSessionModel userSessionModel);
}
