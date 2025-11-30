package org.quijava.quijava.services;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenuService {

    private final SessionDBService sessionDBService;
    private final SessionPreferencesService sessionPreferences;

    public MenuService(SessionDBService sessionDBService, SessionPreferencesService sessionPreferences) {
        this.sessionDBService = sessionDBService;
        this.sessionPreferences = sessionPreferences;
    }

    public Optional<String> getUsername() {
        Optional<String> username = sessionPreferences.getUsername();

        if (username.isPresent()) {
            return username;
        }

        return sessionPreferences.getSessionId()
                .flatMap(sessionDBService::getUsername)
                .map(dbUsername -> {
                    sessionPreferences.setUsername(dbUsername);
                    return dbUsername;
                });
    }

    public Optional<Integer> getRole() {
        Optional<Integer> role = sessionPreferences.getRole();

        if (role.isPresent()) {
            return role;
        }

        return sessionPreferences.getSessionId()
                .flatMap(sessionDBService::getRole)
                .filter(dbRole -> dbRole > 0)
                .map(dbRole -> {
                    sessionPreferences.setRole(dbRole);
                    return dbRole;
                });
    }

    public boolean hasActiveSession() {
        return sessionPreferences.hasActiveSession();
    }

    public void logout() {
        sessionPreferences.getSessionId().ifPresent(sessionDBService::deleteSession);
        sessionPreferences.clear();
    }
}
