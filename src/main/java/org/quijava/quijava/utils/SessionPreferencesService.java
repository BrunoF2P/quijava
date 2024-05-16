package org.quijava.quijava.utils;

import java.util.prefs.Preferences;

public class SessionPreferencesService {
    private static final String USERNAME_KEY = "username";
    private static final String USER_ID_KEY = "user_id";
    private static final String SESSION_ID_KEY = "session_id";
    private static final String SESSION_ROLE_KEY = "role";

    private final Preferences preferences;

    public SessionPreferencesService() {
        this.preferences = Preferences.userRoot().node(this.getClass().getName());
    }

    public void setUsername(String username) {
        preferences.put(USERNAME_KEY, username);
    }


    public String getUsername() {
        return preferences.get(USERNAME_KEY, null);
    }

    public void setUserId(int userId) {
        preferences.putInt(USER_ID_KEY, userId);
    }

    public int getUserId() {
        return preferences.getInt(USER_ID_KEY, -1);
    }

    public void setSessionId(int sessionId) {
        preferences.putInt(SESSION_ID_KEY, sessionId);
    }

    public int getSessionId() {
        return preferences.getInt(SESSION_ID_KEY, -1);
    }


    public void setRole(int role) {
        preferences.putInt(SESSION_ROLE_KEY, role);
    }

    public int getRole() {
        return preferences.getInt(SESSION_ROLE_KEY, -1);
    }

    public void clear() {
        preferences.remove(USERNAME_KEY);
        preferences.remove(USER_ID_KEY);
        preferences.remove(SESSION_ID_KEY);
        preferences.remove(SESSION_ROLE_KEY);
    }
}
