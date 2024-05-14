package org.quijava.quijava;


import javafx.application.Application;
import javafx.stage.Stage;
import org.quijava.quijava.utils.SessionDBService;
import org.quijava.quijava.utils.SessionPreferencesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.quijava.quijava.utils.ScreenLoader;

import java.io.IOException;
import java.util.Optional;


@SpringBootApplication
public class Main extends Application {

    private ConfigurableApplicationContext context;
    private final ScreenLoader screenLoader = new ScreenLoader();
    private final SessionPreferencesService sessionPreferences = new SessionPreferencesService();
    private SessionDBService sessionService;

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

    public static void main(String[] args) {
        launch();
    }


    /**
     *  Se existir sessao armazenada nas preferencias vericica se existe sessao armazenada no banco de dados
     *
     */
    public void checkSessionOnAppOpen(Stage stage) {
        String usernameFromPreferences = sessionPreferences.getUsername();
        int sessionIdFromPreferences = sessionPreferences.getSessionId();

        if (sessionIdFromPreferences != 0) {
            Optional<Integer> sessionId = sessionService.getSessionIdForUser(usernameFromPreferences);
            if (sessionId.isPresent() && sessionService.isSessionValid(sessionId.get())) {
                if (sessionIdFromPreferences == sessionId.get()) {
                    screenLoader.loadMenuScreen(stage, context);
                } else {
                    sessionPreferences.clear();
                    screenLoader.loadLoginScreen(stage, context);
                }
            } else {
                sessionPreferences.clear();
                screenLoader.loadLoginScreen(stage, context);
            }
        } else {
            screenLoader.loadLoginScreen(stage, context);
        }
    }

}