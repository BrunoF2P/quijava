package org.quijava.quijava.services;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@Service
public class SessionPreferencesService {

    private static final String USERNAME_KEY = "username";
    private static final String USER_ID_KEY = "user_id";
    private static final String SESSION_ID_KEY = "session_id";
    private static final String SESSION_ROLE_KEY = "role";

    private final Preferences preferences;

    public SessionPreferencesService() {
        this.preferences = Preferences.userRoot().node(this.getClass().getName());
    }

    public void createPreferencesSession(String username, Integer sessionId, Integer role, Integer userId) {
        if (username != null) preferences.put(USERNAME_KEY, username);
        if (sessionId != null) preferences.putInt(SESSION_ID_KEY, sessionId);
        if (role != null) preferences.putInt(SESSION_ROLE_KEY, role);
        if (userId != null) preferences.putInt(USER_ID_KEY, userId);

        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Erro ao salvar sessão", e);
        }
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(preferences.get(USERNAME_KEY, null));
    }

    public Optional<Integer> getUserId() {
        int userId = preferences.getInt(USER_ID_KEY, -1);
        return userId == -1 ? Optional.empty() : Optional.of(userId);
    }

    public Integer getSessionUserId() {
        return preferences.getInt(USER_ID_KEY, -1);
    }

    public Optional<Integer> getSessionId() {
        int sessionId = preferences.getInt(SESSION_ID_KEY, -1);
        return sessionId == -1 ? Optional.empty() : Optional.of(sessionId);
    }

    public Optional<Integer> getRole() {
        int role = preferences.getInt(SESSION_ROLE_KEY, -1);
        return role == -1 ? Optional.empty() : Optional.of(role);
    }

    public void setUsername(String username) {
        if (username != null) {
            preferences.put(USERNAME_KEY, username);
        } else {
            preferences.remove(USERNAME_KEY);
        }
    }

    public void setUserId(Integer userId) {
        if (userId != null && userId > 0) {
            preferences.putInt(USER_ID_KEY, userId);
        } else {
            preferences.remove(USER_ID_KEY);
        }
    }

    public void setSessionId(Integer sessionId) {
        if (sessionId != null && sessionId > 0) {
            preferences.putInt(SESSION_ID_KEY, sessionId);
        } else {
            preferences.remove(SESSION_ID_KEY);
        }
    }

    public void setRole(Integer role) {
        if (role != null && role >= 0) {
            preferences.putInt(SESSION_ROLE_KEY, role);
        } else {
            preferences.remove(SESSION_ROLE_KEY);
        }
    }

    public void clear() {
        try {
            preferences.clear();
            preferences.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Erro ao limpar sessão", e);
        }
    }

    public boolean hasActiveSession() {
        return getSessionId().isPresent() && getUsername().isPresent();
    }
}
