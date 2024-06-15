package org.quijava.quijava.services;

import org.quijava.quijava.dao.UserSessionDao;
import org.quijava.quijava.models.UserSessionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionDBService {
    private final UserSessionDao userSessionDao;

    @Autowired
    public SessionDBService(UserSessionDao userSessionDao) {
        this.userSessionDao = userSessionDao;
    }

    public void createSession(String username, Integer role, Integer userId) {
        UserSessionModel session = new UserSessionModel();
        session.setUsername(username);
        session.setRole(role);
        session.setUserId(userId);
        session.setCreateTime(LocalDateTime.now());
        userSessionDao.save(session);
    }

    public Integer getLastSessionId(String username) {

        return userSessionDao.getLastSessionIdForUser(username);
    }

    public boolean deleteSession(Integer id) {
        return userSessionDao.delete(id);
    }

    public boolean isSessionValid(Integer id) {
        return userSessionDao.existsById(id);
    }

    public Optional<Integer> getSessionIdForUser(String username) {
        return userSessionDao.getSessionIdForUser(username);
    }

    public String getUsername(Integer id) {
        Optional<UserSessionModel> sessionOptional = userSessionDao.findById(id);
        return sessionOptional.map(UserSessionModel::getUsername).orElse(null);
    }

    public Integer getRole(Integer id) {
        Optional<UserSessionModel> sessionOptional = userSessionDao.findById(id);
        return sessionOptional.map(UserSessionModel::getRole).orElse(null);
    }

    public Integer getUserId(Integer id) {
        Optional<UserSessionModel> sessionOptional = userSessionDao.findById(id);
        return sessionOptional.map(UserSessionModel::getUserId).orElse(null);
    }
}

