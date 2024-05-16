package org.quijava.quijava.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.quijava.quijava.utils.ScreenLoader;
import org.quijava.quijava.utils.SessionDBService;
import org.quijava.quijava.utils.SessionPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ComponentScan
@Component
public class MenuController {

    private final ApplicationContext applicationContext;
    private final SessionDBService sessionDBService;
    private final ScreenLoader screenLoader;


    SessionPreferencesService sessionPreferences= new SessionPreferencesService();

    @FXML
    private Button createQuiz;

    @FXML
    private Button sair;

    @FXML
    private Text role;

    @FXML
    private Text username;





    public void initialize() {
        // Mostrar nome de usuario e permissao de role do mesmo
        username.setText(getUsername());
        role.setText(getRole().toString());
    }

    @FXML
    public String getUsername() {
        String username = sessionPreferences.getUsername();
        if (username == null || username.isEmpty()) {
            username = sessionDBService.getUsername(sessionPreferences.getSessionId());
            sessionPreferences.setUsername(username);
        }
        return username;
    }
    @FXML
    public Integer getRole() {
        Integer role = sessionPreferences.getRole();
        if (role <= 0) {
            role = sessionDBService.getRole(sessionPreferences.getSessionId());
            sessionPreferences.setRole(role);
        }
        return role;
    }


    @FXML
    void onLogout(ActionEvent event) {
        // Remove a sessão atual do banco de dados
        sessionDBService.deleteSession(sessionPreferences.getSessionId());
        // Limpa as preferências da sessão
        sessionPreferences.clear();
        // Navega para a tela de login
        loadLoginScreen();
    }


    @Autowired
    public MenuController(ApplicationContext applicationContext, SessionDBService sessionDBService, ScreenLoader screenLoader) {
        this.applicationContext = applicationContext;
        this.sessionDBService = sessionDBService;
        this.screenLoader = screenLoader;
    }

    @FXML
    void onCreateQuiz(ActionEvent event) {
        loadCreateQuizScreen();
    }

    /**
     * Carrega a de criar quiz
     */
    private void loadCreateQuizScreen() {
        screenLoader.loadCreateQuizScreen((Stage) createQuiz.getScene().getWindow(), applicationContext);
    }


    /**
     * Carrega a tela de login
     */
    private void loadLoginScreen() {
        screenLoader.loadLoginScreen((Stage) sair.getScene().getWindow(), applicationContext);
    }
}


