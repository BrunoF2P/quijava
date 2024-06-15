package org.quijava.quijava.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.services.ImageService;
import org.quijava.quijava.services.QuizService;
import org.quijava.quijava.services.SessionPreferencesService;
import org.quijava.quijava.utils.ScreenLoader;
import org.quijava.quijava.view.ListQuizView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ListQuizController implements Initializable {

    private final ApplicationContext applicationContext;
    private final ScreenLoader screenLoader;
    private final SessionPreferencesService sessionPreferencesService;
    private final QuizService quizService;
    private final ImageService imageService;
    private final ListQuizView listQuizView;
    private CategoryModel category;

    @FXML
    private FlowPane quizzes;

    @FXML
    private Button back;

    @FXML
    private Pagination pagination;

    @Autowired
    public ListQuizController(ApplicationContext applicationContext, ScreenLoader screenLoader, SessionPreferencesService sessionPreferencesService, QuizService quizService, ImageService imageService, ListQuizView listQuizView) {
        this.applicationContext = applicationContext;
        this.screenLoader = screenLoader;
        this.sessionPreferencesService = sessionPreferencesService;
        this.quizService = quizService;
        this.imageService = imageService;
        this.listQuizView = listQuizView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        quizzes.setHgap(15);
        quizzes.setVgap(15);

        if (category != null) {
            List<QuizModel> quizList = quizService.findQuizzesByCategory(category.getId());
            pagination.setPageCount((int) Math.ceil((double) quizList.size() / 6));
            pagination.setPageFactory(pageIndex -> createPage(quizList, pageIndex));
        }
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    private FlowPane createPage(List<QuizModel> quizList, int pageIndex) {
        quizzes.getChildren().clear();

        int fromIndex = pageIndex * 6;
        int toIndex = Math.min(fromIndex + 6, quizList.size());

        for (int i = fromIndex; i < toIndex; i++) {
            QuizModel quiz = quizList.get(i);
            VBox quizBox = listQuizView.createQuizBox(quiz, () -> startQuiz(quiz), () -> showDetailsQuiz(quiz));
            quizzes.getChildren().add(quizBox);
        }

        return quizzes;
    }

    @FXML
    public void goBack() {
        screenLoader.loadAllCategories((Stage) back.getScene().getWindow(), applicationContext);
    }

    @FXML
    public void startQuiz(QuizModel quiz) {
        screenLoader.loadPlayQuiz((Stage) quizzes.getScene().getWindow(), applicationContext, quiz);
    }

    @FXML
    public void showDetailsQuiz(QuizModel quiz) {
        screenLoader.loadDetailsQuizScreen((Stage) quizzes.getScene().getWindow(), applicationContext, quiz);
    }
}
