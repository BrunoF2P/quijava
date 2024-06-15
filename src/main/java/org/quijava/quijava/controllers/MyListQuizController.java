package org.quijava.quijava.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.services.ImageService;
import org.quijava.quijava.services.QuizService;
import org.quijava.quijava.services.SessionPreferencesService;
import org.quijava.quijava.utils.ScreenLoader;
import org.quijava.quijava.view.MyListQuizView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class MyListQuizController implements Initializable {

    private final ApplicationContext applicationContext;
    private final ScreenLoader screenLoader;
    private final SessionPreferencesService sessionPreferencesService;
    private final QuizService quizService;
    private final ImageService imageService;
    private final MyListQuizView myListQuizView;
    @FXML
    private FlowPane quizzes;
    @FXML
    private Button back;
    @FXML
    private Pagination pagination;

    @Autowired
    public MyListQuizController(ApplicationContext applicationContext, ScreenLoader screenLoader, SessionPreferencesService sessionPreferencesService, QuizService quizService, ImageService imageService, MyListQuizView myListQuizView) {
        this.applicationContext = applicationContext;
        this.screenLoader = screenLoader;
        this.sessionPreferencesService = sessionPreferencesService;
        this.quizService = quizService;
        this.imageService = imageService;
        this.myListQuizView = myListQuizView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        quizzes.setHgap(15);
        quizzes.setVgap(15);

        List<QuizModel> quizList = quizService.findAllQuizzesByUserId(sessionPreferencesService.getSessionUserId());

        pagination.setPageCount((int) Math.ceil((double) quizList.size() / 6));
        pagination.setPageFactory(pageIndex -> createPage(quizList, pageIndex));
    }

    private FlowPane createPage(List<QuizModel> quizList, int pageIndex) {
        quizzes.getChildren().clear();

        int fromIndex = pageIndex * 6;
        int toIndex = Math.min(fromIndex + 6, quizList.size());

        for (int i = fromIndex; i < toIndex; i++) {
            QuizModel quiz = quizList.get(i);
            VBox quizBox = myListQuizView.createQuizBox(quiz, () -> deleteQuiz(quiz), () -> editQuiz(quiz), () -> startQuiz(quiz), () -> showDetailsQuiz(quiz));
            quizzes.getChildren().add(quizBox);
        }

        return quizzes;
    }

    private void deleteQuiz(QuizModel quiz) {
        quizService.deleteQuiz(quiz.getId());
        List<QuizModel> quizList = quizService.findAllQuizzesByUserId(sessionPreferencesService.getSessionUserId());
        pagination.setPageCount((int) Math.ceil((double) quizList.size() / 6));
        pagination.setPageFactory(pageIndex -> createPage(quizList, pageIndex));
    }

    @FXML
    public void goBack() {
        screenLoader.loadMenuScreen((Stage) back.getScene().getWindow(), applicationContext);
    }

    @FXML
    public void editQuiz(QuizModel quiz) {

        screenLoader.loadUpdateQuizScreen((Stage) quizzes.getScene().getWindow(), applicationContext, quiz);
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
