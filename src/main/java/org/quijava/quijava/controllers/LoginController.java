package org.quijava.quijava.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@ComponentScan
@Component
public class LoginController {

    private final ScreenLoader screenLoader;
    private final ApplicationContext applicationContext;

    @FXML
    private Text register;

    @Autowired
    public LoginController(ScreenLoader screenLoader, ApplicationContext applicationContext) {
        this.screenLoader = screenLoader;
        this.applicationContext = applicationContext;
    }


    @FXML
    void goToRegister(MouseEvent event) {
        screenLoader.loadRegisterScreen((Stage) register.getScene().getWindow(), applicationContext);
    }









}
