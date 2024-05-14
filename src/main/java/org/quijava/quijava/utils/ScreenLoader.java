package org.quijava.quijava.utils;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ComponentScan
@Component
public class ScreenLoader {

    private void loadScreen(Stage currentStage, String title, Parent root) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        currentStage.setTitle(title);
        currentStage.setScene(scene);
        currentStage.show();
    }

    public void loadMenuScreen(Stage currentStage, ApplicationContext applicationContext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/menuView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(root);

            loadScreen(currentStage, "Menu", borderPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLoginScreen(Stage currentStage, ApplicationContext applicationContext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/loginView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();
            loadScreen(currentStage, "Login", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRegisterScreen(Stage currentStage, ApplicationContext applicationContext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/registerView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();
            loadScreen(currentStage, "Registrar", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
