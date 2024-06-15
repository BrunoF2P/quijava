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
import org.quijava.quijava.services.RegisterService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class RegisterController {

    private final RegisterService registerService;
    private final ScreenLoader screenLoader;
    private final ApplicationContext applicationContext;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField rePasswordField;

    @FXML
    private TextField refCode;

    @FXML
    private Button cadastrarButton;

    @FXML
    private Label alert;

    @FXML
    private Text login;

    @Autowired
    public RegisterController(RegisterService registerService, ScreenLoader screenLoader, ApplicationContext applicationContext) {
        this.registerService = registerService;
        this.screenLoader = screenLoader;
        this.applicationContext = applicationContext;
    }

    @FXML
    void goToLogin(MouseEvent event) {
        screenLoader.loadLoginScreen((Stage) login.getScene().getWindow(), applicationContext);
    }

    @FXML
    void cadastrar(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String rePassword = rePasswordField.getText();
        String ref = refCode.getText();

        try {
            registerService.validateFields(username, password, rePassword);

            int role = registerService.determineUserRole(ref);
            Optional<UserModel> userOptional = registerService.registerUser(username, password, role);

            if (userOptional.isPresent()) {
                registerService.manageUserSession(userOptional.get());
                screenLoader.loadMenuScreen((Stage) cadastrarButton.getScene().getWindow(), applicationContext); // Carregar o menu
            } else {
                setAlert("Erro ao registrar usuário.");
            }
        } catch (IllegalArgumentException e) {
            setAlert(e.getMessage());
        } catch (Exception e) {
            setAlert("Erro durante o registro. Por favor, tente novamente mais tarde.");
            e.printStackTrace();
        }
    }

    @FXML
    private void setAlert(String message) {
        alert.setText(message);
    }
}
