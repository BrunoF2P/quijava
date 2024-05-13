package org.quijava.quijava;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.quijava.quijava.utils.SessionDBService;
import org.quijava.quijava.utils.SessionPreferencesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


import java.io.IOException;
import java.util.Optional;


@SpringBootApplication
public class Main extends Application {
    private ConfigurableApplicationContext context;


    private final SessionPreferencesService sessionPreferences = new SessionPreferencesService();

    private SessionDBService sessionService;

    @Override
    public void init() {
        context = SpringApplication.run(SpringRunApp.class);
        sessionService = context.getBean(SessionDBService.class);
    }


    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        String cssStyle = getClass().getResource("/css/styles.css").toExternalForm();
        checkSessionOnAppOpen(cssStyle);
    }

    /**
     * Fecha o Spring ao encerrar a aplicação
     */
    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch();
    }


    /**
     *  Se existir sessao armazenada nas preferencias vericica se existe sessao armazenada no banco de dados
     *
     */
    public void checkSessionOnAppOpen(String cssStyle) {
        String usernameFromPreferences = sessionPreferences.getUsername();
        int sessionIdFromPreferences = sessionPreferences.getSessionId();

        if (sessionIdFromPreferences != 0) {
            Optional<Integer> sessionId = sessionService.getSessionIdForUser(usernameFromPreferences);
            if (sessionId.isPresent() && sessionService.isSessionValid(sessionId.get())) {
                navigateToMenuScreen(cssStyle);
            } else {
                navigateToLoginScreen(cssStyle);
            }
        } else {
            navigateToLoginScreen(cssStyle);
        }
    }

    /**
     * Navega até o menu
     */
    private void navigateToMenuScreen(String cssStyle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menuView.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(cssStyle);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Navega até a tela de login
     */
    private void navigateToLoginScreen(String cssStyle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loginView.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(cssStyle);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}