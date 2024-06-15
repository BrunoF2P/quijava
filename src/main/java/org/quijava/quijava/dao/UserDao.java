package org.quijava.quijava.dao;

import org.quijava.quijava.models.UserModel;

public interface UserDao {
    void save(UserModel user);

    void update(UserModel user);

    void delete(UserModel user);

    UserModel findById(Integer id);

    UserModel findByUsername(String username);

    boolean existsByUsername(String username);
}
