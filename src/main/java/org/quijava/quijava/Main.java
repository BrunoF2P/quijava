package org.quijava.quijava;


import javafx.application.Application;
import javafx.stage.Stage;
import org.quijava.quijava.models.SessionPreferencesModel;
import org.quijava.quijava.services.SessionDBService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Optional;


@SpringBootApplication
public class Main extends Application {

    private final ScreenLoader screenLoader = new ScreenLoader();
    private final SessionPreferencesModel sessionPreferencesModel = new SessionPreferencesModel();
    private ConfigurableApplicationContext context;
    private SessionDBService sessionService;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() {
        context = SpringApplication.run(SpringRunApp.class);
        sessionService = context.getBean(SessionDBService.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        checkSessionOnAppOpen(stage);
    }

    /**
     * Fecha o Spring ao encerrar a aplicação
     */
    @Override
    public void stop() {
        context.close();
    }

    /**
     * Se existir sessao armazenada nas preferencias vericica se existe sessao armazenada no banco de dados
     */
    public void checkSessionOnAppOpen(Stage stage) {
        String usernameFromPreferences = sessionPreferencesModel.getUsername();
        int sessionIdFromPreferences = sessionPreferencesModel.getSessionId();

        if (sessionIdFromPreferences != 0) {
            Optional<Integer> sessionId = sessionService.getSessionIdForUser(usernameFromPreferences);
            if (sessionId.isPresent() && sessionService.isSessionValid(sessionId.get())) {
                if (sessionIdFromPreferences == sessionId.get()) {
                    screenLoader.loadMenuScreen(stage, context);
                } else {
                    sessionPreferencesModel.clear();
                    screenLoader.loadLoginScreen(stage, context);
                }
            } else {
                sessionPreferencesModel.clear();
                screenLoader.loadLoginScreen(stage, context);
            }
        } else {
            screenLoader.loadLoginScreen(stage, context);
        }
    }

}