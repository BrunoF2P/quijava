package org.quijava.quijava.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.quijava.quijava.services.MenuService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;


@Controller
public class MenuController {


    private final ApplicationContext applicationContext;
    private final ScreenLoader screenLoader;
    private final MenuService menuService;

    @FXML
    private Button createQuiz;

    @FXML
    private Button sair;

    @FXML
    private Text role;

    @FXML
    private Text username;

    @Autowired
    public MenuController(ApplicationContext applicationContext, ScreenLoader screenLoader, MenuService menuService) {
        this.applicationContext = applicationContext;
        this.screenLoader = screenLoader;
        this.menuService = menuService;
    }

    public void initialize() {
        username.setText(menuService.getUsername());
        role.setText(menuService.getRole().toString());
    }

    @FXML
    void onLogout(ActionEvent event) {
        menuService.logout();
        screenLoader.loadLoginScreen((Stage) sair.getScene().getWindow(), applicationContext);
    }

    @FXML
    void onCreateQuiz(ActionEvent event) {
        screenLoader.loadCreateQuizScreen((Stage) createQuiz.getScene().getWindow(), applicationContext);
    }
}


