package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.quijava.quijava.models.UserModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional

public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(UserModel user) {
        entityManager.persist(user);
    }

    @Override
    public void update(UserModel user) {
        entityManager.merge(user);
    }

    @Override
    public void delete(UserModel user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    @Override
    public UserModel findById(Integer id) {
        return entityManager.find(UserModel.class, id);
    }

    @Override
    public UserModel findByUsername(String username) {
        try {
            return entityManager.createQuery("SELECT u FROM UserModel u WHERE u.username = :username", UserModel.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM UserModel u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
}
