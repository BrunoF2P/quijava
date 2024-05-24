package org.quijava.quijava.services;

import org.quijava.quijava.models.SessionPreferencesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuService {


    private final SessionDBService sessionDBService;
    private final SessionPreferencesModel sessionPreferencesModel;

    @Autowired
    public MenuService(SessionDBService sessionDBService, SessionPreferencesModel sessionPreferencesModel) {
        this.sessionDBService = sessionDBService;
        this.sessionPreferencesModel = sessionPreferencesModel;
    }

    public String getUsername() {
        String username = sessionPreferencesModel.getUsername();
        if (username == null || username.isEmpty()) {
            username = sessionDBService.getUsername(sessionPreferencesModel.getSessionId());
            sessionPreferencesModel.setUsername(username);
        }
        return username;
    }

    public Integer getRole() {
        Integer role = sessionPreferencesModel.getRole();
        if (role == null || role <= 0) {
            role = sessionDBService.getRole(sessionPreferencesModel.getSessionId());
            sessionPreferencesModel.setRole(role);
        }
        return role;
    }

    public void logout() {
        sessionDBService.deleteSession(sessionPreferencesModel.getSessionId());
        sessionPreferencesModel.clear();
    }
}
