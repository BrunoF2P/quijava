package org.quijava.quijava.services;

import org.quijava.quijava.dao.UserDao;
import org.quijava.quijava.models.UserModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {

    private static final int ADMIN_ROLE = 2;
    private static final int USER_ROLE = 1;

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final SessionPreferencesService sessionPreferences;

    public AdminService(UserDao userDao,
                        PasswordEncoder passwordEncoder,
                        SessionPreferencesService sessionPreferences) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.sessionPreferences = sessionPreferences;
    }

    public void createAdmin(String username, String password) {
        validateRequesterIsAdmin();
        validateAdminData(username, password);

        UserModel admin = new UserModel();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(ADMIN_ROLE);

        userDao.save(admin);
    }

    private void validateRequesterIsAdmin() {
        Integer currentRole = sessionPreferences.getRole()
                .orElseThrow(() -> new IllegalStateException("Usuário não está logado"));

        if (currentRole != ADMIN_ROLE) {
            throw new IllegalStateException("Apenas administradores podem criar novos admins");
        }
    }

    private void validateAdminData(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres");
        }

        if (userDao.existsByUsername(username)) {
            throw new IllegalArgumentException("Username já existe");
        }
    }
}
