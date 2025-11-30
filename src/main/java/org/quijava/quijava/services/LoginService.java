package org.quijava.quijava.services;

import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.models.UserModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final SessionDBService sessionDBService;
    private final PasswordEncoder passwordEncoder;
    private final SessionPreferencesService sessionPreferences;
    private final UserDao userDao;

    public LoginService(SessionDBService sessionDBService,
                        PasswordEncoder passwordEncoder,
                        SessionPreferencesService sessionPreferences,
                        UserDao userDao) {
        this.sessionDBService = sessionDBService;
        this.passwordEncoder = passwordEncoder;
        this.sessionPreferences = sessionPreferences;
        this.userDao = userDao;
    }

    public boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }

        return userDao.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public Optional<UserModel> getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public Optional<Integer> getUserRole(String username) {
        return getUserByUsername(username).map(UserModel::getRole);
    }

    public void createFullSession(String username, Integer role, Integer userId) {
        deleteActiveSession(username);
        sessionDBService.createSession(username, role, userId);
        Integer sessionId = sessionDBService.getLastSessionId(username);
        sessionPreferences.createPreferencesSession(username, sessionId, role, userId);
    }

    public Integer getLastSessionId(String username) {
        return sessionDBService.getLastSessionId(username);
    }

    public void deleteActiveSession(String username) {
        sessionDBService.getSessionIdForUser(username)
                .ifPresent(sessionDBService::deleteSession);
    }

    public Optional<UserModel> findById(Integer id) {
        return userDao.findById(id);
    }
}
