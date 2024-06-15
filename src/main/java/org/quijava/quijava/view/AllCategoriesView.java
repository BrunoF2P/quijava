package org.quijava.quijava.view;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.quijava.quijava.controllers.ListQuizController;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AllCategoriesView {

    private final ListQuizController listQuizController;
    private final ScreenLoader screenLoader;
    private final ApplicationContext applicationContext;

    @Autowired
    public AllCategoriesView(ListQuizController listQuizController, ScreenLoader screenLoader, ApplicationContext applicationContext) {
        this.listQuizController = listQuizController;
        this.screenLoader = screenLoader;
        this.applicationContext = applicationContext;
    }

    public void createCategoryButton(FlowPane categories, CategoryModel category) {
        Button button = new Button(category.getDescription());
        button.setMinWidth(300);
        button.setMinHeight(100);
        button.setStyle("-fx-font-size: 20px;");

        button.setOnAction(event -> {
            System.out.println("Botão " + category.getDescription() + " clicado.");

            Platform.runLater(() -> {
                try {
                    listQuizController.setCategory(category);
                    screenLoader.loadQuizzesScreen((Stage) button.getScene().getWindow(), applicationContext, category); // Chama o método para carregar a tela de quizzes
                } catch (IllegalStateException e) {
                    System.err.println("Erro ao definir a categoria: " + e.getMessage());
                }
            });
        });

        categories.getChildren().add(button);
    }
}
