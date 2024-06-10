package org.quijava.quijava.services;

import org.quijava.quijava.models.SessionPreferencesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionPreferencesService {
    private final SessionPreferencesModel sessionPreferences;


    @Autowired
    public SessionPreferencesService(SessionPreferencesModel sessionPreferences) {
        this.sessionPreferences = sessionPreferences;
    }

    public void createPreferencesSession(String username, Integer sessionId, Integer role, Integer userId) {
        sessionPreferences.setUsername(username);
        sessionPreferences.setSessionId(sessionId);
        sessionPreferences.setRole(role);
        sessionPreferences.setUserId(userId);
    }

    public Integer getSessionUserId() {
        return sessionPreferences.getUserId();
    }

}