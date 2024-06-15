package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.quijava.quijava.models.UserSessionModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class UserSessionDaoImpl implements UserSessionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsById(Integer id) {
        return entityManager.createQuery("SELECT COUNT(u) FROM UserSessionModel u WHERE u.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    @Override
    public Optional<UserSessionModel> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(UserSessionModel.class, id));
    }

    @Override
    public boolean delete(Integer id) {
        Optional<UserSessionModel> sessionOptional = findById(id);
        if (sessionOptional.isPresent()) {
            entityManager.remove(sessionOptional.get());
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(Integer id) {
        entityManager.createQuery("DELETE FROM UserSessionModel u WHERE u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Integer getLastSessionIdForUser(String username) {
        return entityManager.createQuery("SELECT MAX(u.id) FROM UserSessionModel u WHERE u.username = :username", Integer.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    public Optional<Integer> getSessionIdForUser(String username) {
        return entityManager.createQuery("SELECT u.id FROM UserSessionModel u WHERE u.username = :username", Integer.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public void save(UserSessionModel userSessionModel) {
        entityManager.persist(userSessionModel);
    }
}
