package org.quijava.quijava.controllers;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
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
    private Button createCategory;

    @FXML
    private Button sair;

    @FXML
    private Text idSession;

    @FXML
    private Text username;





    public void initialize() {
        // Obtém o nome de usuário e o ID da sessão e define nos campos de texto correspondentes
        username.setText(getUsername());
        idSession.setText(getRole().toString());
    }

    @FXML
    public String getUsername() {
        return sessionDBService.getUsername(sessionPreferences.getSessionId());
    }
    @FXML
    public Integer getRole() {
        return sessionDBService.getRole(sessionPreferences.getSessionId());
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

    @FXML
    void onCreateCategory(ActionEvent event) {
        loadCreateCategoryScreen();
    }

    private void loadCreateQuizScreen(){
        try {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            String cssStyle = getClass().getResource("/css/styles.css").toExternalForm();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/createQuizView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent createQuizView = fxmlLoader.load();


            BorderPane root = (BorderPane) createQuiz.getScene().getRoot();

            root.setCenter(createQuizView);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCreateCategoryScreen(){
        try {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            String cssStyle = getClass().getResource("/css/styles.css").toExternalForm();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/createCategoryView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent createCategoryView = fxmlLoader.load();


            BorderPane root = (BorderPane) createQuiz.getScene().getRoot();

            root.setCenter(createCategoryView);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Carrega a tela de login
     */
    private void loadLoginScreen() {
        screenLoader.loadLoginScreen((Stage) sair.getScene().getWindow(), applicationContext);
    }
}


