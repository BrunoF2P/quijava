package org.quijava.quijava.utils;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.quijava.quijava.controllers.*;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.QuizModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ComponentScan
@Component
public class ScreenLoader {

    private void loadScreen(Stage currentStage, String title, Parent root) {
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        currentStage.setTitle(title);
        currentStage.setScene(scene);
        currentStage.setMinWidth(700);
        currentStage.setMinHeight(800);

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

    public void loadCreateQuizScreen(Stage currentStage, ApplicationContext applicationContext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/createQuizView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            loadScreen(currentStage, "Criar Quiz", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCreateCategoryScreen(ApplicationContext applicationContext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/createCategoryView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Criar Categoria");

            // Carrega o estilo CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setMinWidth(500);
            stage.setMinHeight(500);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCreateQuestionScreen(Stage currentStage, ApplicationContext applicationContext, QuizModel quiz) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/createQuestionView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            CreateQuestionController controller = fxmlLoader.getController();

            controller.setQuizModel(quiz);

            loadScreen(currentStage, "Criar Perguntas", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllCategories(Stage currentStage, ApplicationContext applicationContext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/allCategoriesView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            loadScreen(currentStage, "Todas as categorias", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMyQuizzes(Stage currentStage, ApplicationContext applicationContext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/myListQuizzesView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            loadScreen(currentStage, "Meus Quizzes", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPlayQuiz(Stage currentStage, ApplicationContext applicationContext, QuizModel quiz) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/playQuizView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            PlayQuizController controller = fxmlLoader.getController();
            controller.setQuiz(quiz);

            loadScreen(currentStage, "Jogar", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUpdateQuizScreen(Stage currentStage, ApplicationContext applicationContext, QuizModel quiz) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/updateQuizView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            UpdateQuizController controller = fxmlLoader.getController();
            controller.setQuiz(quiz);

            loadScreen(currentStage, "Editar Quiz", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDetailsQuizScreen(Stage currentStage, ApplicationContext applicationContext, QuizModel quiz) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/detailsQuizView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            DetailsQuizController controller = fxmlLoader.getController();
            controller.setQuiz(quiz);

            loadScreen(currentStage, "Detalhes", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadQuizzesScreen(Stage currentStage, ApplicationContext applicationContext, CategoryModel categoryModel) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/quijava/quijava/listQuizzesView.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();

            ListQuizController controller = fxmlLoader.getController();
            controller.setCategory(categoryModel);

            loadScreen(currentStage, "Quizzes", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
