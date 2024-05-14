package org.quijava.quijava.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.quijava.quijava.models.UserModel;
import org.quijava.quijava.repositories.UserRepository;
import org.quijava.quijava.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Optional;


@ComponentScan
@Component
public class LoginController {

    private final ScreenLoader screenLoader;
    private final ApplicationContext applicationContext;
    private final UserRepository userRepository;
    private final SessionDBService sessionDBService;
    private final PasswordEncoder passwordEncoder;
    SessionPreferencesService sessionPreferences= new SessionPreferencesService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label alert;

    @FXML
    private Text register;

    @Autowired
    public LoginController(ScreenLoader screenLoader, ApplicationContext applicationContext, UserRepository userRepository, SessionDBService sessionDBService, PasswordEncoder passwordEncoder) {
        this.screenLoader = screenLoader;
        this.applicationContext = applicationContext;
        this.userRepository = userRepository;
        this.sessionDBService = sessionDBService;
        this.passwordEncoder = passwordEncoder;
    }

    @FXML
    void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();


        // Valida o login
        if (!validateLogin(username, password)) {
            // Caso contrário, exiba uma mensagem de erro
            setAlert("Usuário ou senha incorretos!");
            return;
        }

        // Se o login for válido, exclui qualquer sessão ativa existente
        Optional<Integer> activeSessionIdOptional = sessionDBService.getSessionIdForUser(username);
        if (activeSessionIdOptional.isPresent()) {
            Integer activeSessionId = activeSessionIdOptional.get();
            sessionDBService.deleteSession(activeSessionId);
        }


        createSession(username, getUserRole(username));
        Integer sessionId = sessionDBService.getLastSessionId(username);
        createPreferencesSession(username, sessionId, getUserRole(username));
        loadMenuScreen();
    }


    /**
     * Obtém a role do usuário
     * @param username nome de usuário
     * @return role do usuário
     */
    private Integer getUserRole(String username) {
        Optional<UserModel> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        return userOptional.map(UserModel::getRole).orElse(null);
    }

    /**
     * Método para validar o login
     * @param username - nome do usuario
     * @param password - senha do usuario
     * @return verificacao se senha e usurio é valida
     */

    private boolean validateLogin(String username, String password) {
        Optional<UserModel> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    private void createSession(String username, Integer role){
        sessionDBService.createSession(username, role);
    }

    @FXML
    void goToRegister(MouseEvent event) {
        screenLoader.loadRegisterScreen((Stage) register.getScene().getWindow(), applicationContext);
    }

    private void createPreferencesSession(String username, Integer sessionId, Integer role){
        sessionPreferences.setUsername(username);
        sessionPreferences.setSessionId(sessionId);
        sessionPreferences.setRole(role);
    }

    private void setAlert(String message) {
        alert.setText(message);
    }

    /**
     * Carrega a tela de menu
     */
    private void loadMenuScreen() {
        screenLoader.loadMenuScreen((Stage) register.getScene().getWindow(), applicationContext);

    }


}
