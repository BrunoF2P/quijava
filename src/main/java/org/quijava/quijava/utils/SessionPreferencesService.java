package org.quijava.quijava.utils;

import java.util.prefs.Preferences;

public class SessionPreferencesService {
    private static final String USERNAME_KEY = "username";
    private static final String SESSION_ID_KEY = "session_id";

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

    public void setSessionId(int sessionId) {
        preferences.putInt(SESSION_ID_KEY, sessionId);
    }

    public int getSessionId() {
        return preferences.getInt(SESSION_ID_KEY, -1);
    }

    public void clear() {
        preferences.remove(USERNAME_KEY);
        preferences.remove(SESSION_ID_KEY);
    }
}
