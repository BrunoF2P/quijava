package org.quijava.quijava;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class Main extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = SpringApplication.run(SpringRunApp.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        String cssStyle = getClass().getResource("/css/styles.css").toExternalForm();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registerView.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 535, 768);
        scene.getStylesheets().add(cssStyle);

        stage.setTitle("Cadastrar");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Fecha o contexto do Spring ao encerrar a aplicação
     */
    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch();
    }
}