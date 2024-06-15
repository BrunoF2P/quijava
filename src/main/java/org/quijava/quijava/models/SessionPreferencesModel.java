package org.quijava.quijava.models;

import org.springframework.stereotype.Service;

import java.util.prefs.Preferences;

@Service
public class SessionPreferencesModel {
    private static final String USERNAME_KEY = "username";
    private static final String USER_ID_KEY = "user_id";
    private static final String SESSION_ID_KEY = "session_id";
    private static final String SESSION_ROLE_KEY = "role";

    private final Preferences preferences;

    public SessionPreferencesModel() {
        this.preferences = Preferences.userRoot().node(this.getClass().getName());
    }

    public String getUsername() {
        return preferences.get(USERNAME_KEY, null);
    }

    public void setUsername(String username) {
        preferences.put(USERNAME_KEY, username);
    }

    public int getUserId() {
        return preferences.getInt(USER_ID_KEY, -1);
    }

    public void setUserId(int userId) {
        preferences.putInt(USER_ID_KEY, userId);
    }

    public int getSessionId() {
        return preferences.getInt(SESSION_ID_KEY, -1);
    }

    public void setSessionId(int sessionId) {
        preferences.putInt(SESSION_ID_KEY, sessionId);
    }

    public int getRole() {
        return preferences.getInt(SESSION_ROLE_KEY, -1);
    }

    public void setRole(int role) {
        preferences.putInt(SESSION_ROLE_KEY, role);
    }

    public void clear() {
        preferences.remove(USERNAME_KEY);
        preferences.remove(USER_ID_KEY);
        preferences.remove(SESSION_ID_KEY);
        preferences.remove(SESSION_ROLE_KEY);
    }
}
