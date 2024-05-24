package org.quijava.quijava.services;

import org.quijava.quijava.models.UserModel;
import org.quijava.quijava.repositories.UserRepository;
import org.quijava.quijava.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class RegisterService {


    private final PasswordEncoder passwordEncoder;
    public final SessionDBService sessionDBService;
    private final SessionPreferencesService sessionPreferencesService;
    private final UserRepository userRepository;

    @Autowired
    public RegisterService(PasswordEncoder passwordEncoder, SessionDBService sessionDBService, SessionPreferencesService sessionPreferencesService, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.sessionDBService = sessionDBService;
        this.sessionPreferencesService = sessionPreferencesService;
        this.userRepository = userRepository;
    }

    public boolean validateUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9]+$";
        return Pattern.matches(usernameRegex, username);
    }

    public boolean validatePassword(String password) {
        return password.length() >= 8;
    }

    public boolean checkPasswordsMatch(String password, String rePassword) {
        return password.equals(rePassword);
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void validateFields(String username, String password, String rePassword) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty() || rePassword == null || rePassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }
        if (!checkPasswordsMatch(password, rePassword)) {
            throw new IllegalArgumentException("As senhas não correspondem.");
        }
        if (!validateUsername(username)) {
            throw new IllegalArgumentException("O nome de usuário deve conter apenas letras e números.");
        }
        if (!validatePassword(password)) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (userExists(username)) {
            throw new IllegalArgumentException("Nome de usuário já está em uso.");
        }
    }

    public int determineUserRole(String refCode) {
        return "Larissa".equals(refCode) ? 2 : 1;
    }

    public Optional<UserModel> registerUser(String username, String password, int role) {


        UserModel newUser = new UserModel();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encodePassword(password));
        newUser.setRole(role);
        userRepository.save(newUser);

        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public void manageUserSession(UserModel user) throws Exception {
        String username = user.getUsername();
        Integer userId = user.getId();
        Integer role = user.getRole();

        sessionDBService.createSession(username, role, userId);
        Integer sessionId = sessionDBService.getLastSessionId(username);
        sessionPreferencesService.createPreferencesSession(username, sessionId, role, userId);
    }
}
