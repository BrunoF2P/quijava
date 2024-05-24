package org.quijava.quijava.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.quijava.quijava.models.UserModel;
import org.quijava.quijava.services.LoginService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class LoginController {

    private final ScreenLoader screenLoader;
    private final LoginService loginService;
    private final ApplicationContext applicationContext;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label alert;

    @FXML
    private Text register;

    @FXML
    private Button loginButton;

    @Autowired
    public LoginController(ScreenLoader screenLoader, LoginService loginService, ApplicationContext applicationContext) {
        super();
        this.screenLoader = screenLoader;
        this.loginService = loginService;
        this.applicationContext = applicationContext;
    }

    @FXML
    void goToRegister(MouseEvent event) {
        screenLoader.loadRegisterScreen((Stage) register.getScene().getWindow(), applicationContext);
    }

    @FXML
    void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (loginService.validateLogin(username, password)) {
                Optional<UserModel> userOptional = loginService.getUserByUsername(username);
                if (userOptional.isPresent()) {
                    UserModel user = userOptional.get();
                    Integer userId = user.getId();
                    Integer role = user.getRole();

                    loginService.deleteActiveSession(username);

                    loginService.createSession(username, role, userId);
                    Integer sessionId = loginService.getLastSessionId(username);

                    loginService.createPreferencesSession(username, sessionId, role, userId);

                    screenLoader.loadMenuScreen((Stage) loginButton.getScene().getWindow(), applicationContext); // Carrega o menu
                    return;
                }
            }
            setAlert("Usuário ou senha incorretos.");
        } catch (IllegalArgumentException e) {
            setAlert(e.getMessage());
        } catch (Exception e) {
            setAlert("Erro durante o login. Por favor, tente novamente mais tarde.");
            e.printStackTrace();
        }
    }

    @FXML
    private void setAlert(String message) {
        alert.setText(message);
    }

}
