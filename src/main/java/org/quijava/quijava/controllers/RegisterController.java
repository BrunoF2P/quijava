package org.quijava.quijava.controllers;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.quijava.quijava.models.UserModel;
import org.quijava.quijava.repositories.UserRepository;
import org.quijava.quijava.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;


@ComponentScan
@Component
public class RegisterController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField rePasswordField;

    @FXML
    private Button cadastrarButton;

    @FXML
    private Label alert;

    @FXML
    private Text login;

    @Autowired
    public RegisterController(PasswordEncoder passwordEncoder, UserRepository userRepository, ApplicationContext applicationContext) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
    }


    @FXML
    void goToLogin(MouseEvent event) {
        // Carregar a tela de login aqui
        loadLoginScreen();
    }

    @FXML
    void cadastrar(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String rePassword = rePasswordField.getText();

        if (userValidation(username, password, rePassword)) {
            createUser(username, password);
            showAlert("Usuário cadastrado com sucesso!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cadastro de Usuário");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Cria um novo usuário e salva no banco de dados
     */
    private void createUser(String username, String password){
        UserModel newUser = new UserModel();
        newUser.setUsername(username);
        newUser.setPassword(password);
        String encodedPassword = passwordEncoder.encodePassword(newUser.getPassword());
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);
    }

    /**
     * Verifica se os campos estão vazios e se senha é maior que 8 e se o usuario existe
     */
    private boolean userValidation(String username, String password, String rePassword){

        if (username.isEmpty() || password.isEmpty()) {  // verifica se os campos de username e password estão vazios
            setAlert("Preencha todos os campos.");
            return false;
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {  // verifica se o username so possui letras e numeros
            setAlert("O nome de usuário deve conter apenas letras e números");
            return false;
        }

        if (!password.equals(rePassword)) { // Verifica se as senhas são iguais
            setAlert("As senhas não correspondem.");
            return false;
        }

        if (password.length() < 8) {  // verifica se a senha é menor que 8
            setAlert("A senha deve ter pelo menos 8 caracteres.");
            return false;
        }

        if (userRepository.existsByUsername(username)) { // verifica se o usuario existe
            setAlert("Nome de usuário já está em uso.");
            return false;
        }

        return true;
    }

    private void setAlert(String message) {
        alert.setText(message);
    }

    private void loadLoginScreen(){
        try {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            String cssStyle = getClass().getResource("/css/styles.css").toExternalForm();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/loginView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();


            Scene scene = new Scene(root, 1280, 768);
            scene.getStylesheets().add(cssStyle);
            Stage stage = (Stage) login.getScene().getWindow();
            stage.setTitle("Entrar");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
