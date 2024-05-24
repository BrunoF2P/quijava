package org.quijava.quijava.services;

import org.quijava.quijava.models.UserSessionModel;
import org.quijava.quijava.repositories.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionDBService {
    private final UserSessionRepository sessionRepository;

    @Autowired
    public SessionDBService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void createSession(String username, Integer role, Integer userId) {
        UserSessionModel session = new UserSessionModel();
        session.setUsername(username);
        session.setRole(role);
        session.setUserId(userId);
        session.setCreateTime(LocalDateTime.now());
        sessionRepository.save(session);
    }
    public Integer getLastSessionId(String username) {

        return sessionRepository.getLastSessionIdForUser(username);
    }

    public boolean deleteSession(Integer id) {
        Optional<UserSessionModel> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isPresent()) {
            sessionRepository.delete(sessionOptional.get());
            return true;
        }
        return false;
    }

    public boolean isSessionValid(Integer id) {
        return sessionRepository.existsById(id);
    }

    public Optional<Integer> getSessionIdForUser(String username) {
        return sessionRepository.getSessionIdForUser(username);
    }

    public String getUsername(Integer id) {
        Optional<UserSessionModel> sessionOptional = sessionRepository.findById(id);
        return sessionOptional.map(UserSessionModel::getUsername).orElse(null);
    }

    public Integer getRole(Integer id) {
        Optional<UserSessionModel> sessionOptional = sessionRepository.findById(id);
        return sessionOptional.map(UserSessionModel::getRole).orElse(null);
    }

    public Integer getUserId(Integer id) {
        Optional<UserSessionModel> sessionOptional = sessionRepository.findById(id);
        return sessionOptional.map(UserSessionModel::getUserId).orElse(null);
    }
}

