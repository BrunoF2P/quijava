package org.quijava.quijava.services;

import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.models.UserModel;
import org.quijava.quijava.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final SessionDBService sessionDBService;
    private final PasswordEncoder passwordEncoder;
    private final SessionPreferencesService sessionPreferencesService;
    private final UserDao userDao;

    @Autowired
    public LoginService(SessionDBService sessionDBService, PasswordEncoder passwordEncoder, SessionPreferencesService sessionPreferencesService, UserDao userDao) {
        this.sessionDBService = sessionDBService;
        this.passwordEncoder = passwordEncoder;
        this.sessionPreferencesService = sessionPreferencesService;
        this.userDao = userDao;
    }

    public boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }

        Optional<UserModel> userOptional = Optional.ofNullable(userDao.findByUsername(username));
        return userOptional.map(user -> passwordEncoder.matches(password, user.getPassword())).orElse(false);
    }

    public Optional<UserModel> getUserByUsername(String username) {
        return Optional.ofNullable(userDao.findByUsername(username));
    }

    public Integer getUserRole(String username) {
        return getUserByUsername(username).map(UserModel::getRole).orElse(null);
    }

    public void createSession(String username, Integer role, Integer userId) {
        sessionDBService.createSession(username, role, userId);
    }

    public Integer getLastSessionId(String username) {
        return sessionDBService.getLastSessionId(username);
    }

    public void deleteActiveSession(String username) {
        sessionDBService.getSessionIdForUser(username).ifPresent(sessionDBService::deleteSession);
    }

    public void createPreferencesSession(String username, Integer sessionId, Integer role, Integer userId) {
        sessionPreferencesService.createPreferencesSession(username, sessionId, role, userId);
    }

    public UserModel findById(Integer id) {
        return userDao.findById(id);
    }
}
