package org.quijava.quijava.controllers;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


import java.io.IOException;

@ComponentScan
@Component
public class LoginController {

    private final ApplicationContext applicationContext;

    @FXML
    private Text register;

    @Autowired
    public LoginController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @FXML
    void goToRegister(MouseEvent event) {
        loadRegisterScreen();
    }




    private void loadRegisterScreen() {
        try {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            String cssStyle = getClass().getResource("/css/styles.css").toExternalForm();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/registerView.fxml"));

            fxmlLoader.setControllerFactory(applicationContext::getBean);

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1280, 768);
            scene.getStylesheets().add(cssStyle);
            Stage stage = (Stage) register.getScene().getWindow();
            stage.setTitle("Cadastrar");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
