package org.quijava.quijava.services;

import org.quijava.quijava.dao.UserSessionDao;
import org.quijava.quijava.models.UserSessionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SessionDBService {

    private final UserSessionDao userSessionDao;

    public SessionDBService(UserSessionDao userSessionDao) {
        this.userSessionDao = userSessionDao;
    }

    public void createSession(String username, Integer role, Integer userId) {
        UserSessionModel session = new UserSessionModel();
        session.setUsername(username);
        session.setRole(role);
        session.setUserId(userId);
        userSessionDao.save(session);
    }

    public Integer getLastSessionId(String username) {
        return userSessionDao.getLastSessionIdForUser(username);
    }

    public void deleteSession(Integer id) {
        userSessionDao.deleteById(id);
    }

    public boolean isSessionValid(Integer id) {
        return userSessionDao.existsById(id);
    }

    public Optional<Integer> getSessionIdForUser(String username) {
        return userSessionDao.findByUsername(username)
                .map(UserSessionModel::getId);
    }

    public Optional<String> getUsername(Integer id) {
        return userSessionDao.findById(id)
                .map(UserSessionModel::getUsername);
    }

    public Optional<Integer> getRole(Integer id) {
        return userSessionDao.findById(id)
                .map(UserSessionModel::getRole);
    }

    public Optional<Integer> getUserId(Integer id) {
        return userSessionDao.findById(id)
                .map(UserSessionModel::getUserId);
    }
}
