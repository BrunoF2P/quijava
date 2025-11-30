package org.quijava.quijava.services;

import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.models.UserModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional
public class RegisterService {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9]+$";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int USER_ROLE = 1;

    private final PasswordEncoder passwordEncoder;
    private final SessionDBService sessionDBService;
    private final SessionPreferencesService sessionPreferences;
    private final UserDao userDao;

    public RegisterService(PasswordEncoder passwordEncoder,
                           SessionDBService sessionDBService,
                           SessionPreferencesService sessionPreferences,
                           UserDao userDao) {
        this.passwordEncoder = passwordEncoder;
        this.sessionDBService = sessionDBService;
        this.sessionPreferences = sessionPreferences;
        this.userDao = userDao;
    }

    public void validateFields(String username, String password, String rePassword) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                rePassword == null || rePassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }

        if (!password.equals(rePassword)) {
            throw new IllegalArgumentException("As senhas não correspondem.");
        }

        if (!Pattern.matches(USERNAME_REGEX, username)) {
            throw new IllegalArgumentException("O nome de usuário deve conter apenas letras e números.");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }

        if (userDao.existsByUsername(username)) {
            throw new IllegalArgumentException("Nome de usuário já está em uso.");
        }
    }

    public UserModel registerUser(String username, String password) {
        UserModel newUser = new UserModel();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(USER_ROLE);

        return userDao.save(newUser);
    }

    public void manageUserSession(UserModel user) {
        String username = user.getUsername();
        Integer userId = user.getId();
        Integer role = user.getRole();

        sessionDBService.createSession(username, role, userId);
        Integer sessionId = sessionDBService.getLastSessionId(username);
        sessionPreferences.createPreferencesSession(username, sessionId, role, userId);
    }
}
