package org.quijava.quijava.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.RankingModel;
import org.quijava.quijava.services.RankService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class DetailsQuizController implements Initializable {

    private final RankService rankService;
    private final ScreenLoader screenLoader;
    private final ApplicationContext applicationContext;

    @FXML
    private Button back;
    @FXML
    private Button quizzes;

    @FXML
    public ListView<RankingModel> listViewRankings;
    private QuizModel quiz;

    @Autowired
    public DetailsQuizController(RankService rankService, ScreenLoader screenLoader, ApplicationContext applicationContext) {
        this.rankService = rankService;
        this.screenLoader = screenLoader;
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
        loadRankings();
    }

    @FXML
    public void goBack() {
        screenLoader.loadMenuScreen((Stage) back.getScene().getWindow(), applicationContext);
    }

    @FXML
    public void startQuiz(QuizModel quiz) {
        screenLoader.loadPlayQuiz((Stage) quizzes.getScene().getWindow(), applicationContext, quiz);
    }

    private void loadRankings() {
        ObservableList<RankingModel> rankings = rankService.getAllRankingsSorted(quiz);
        listViewRankings.setItems(rankings);
    }

    public void startQuiz(ActionEvent actionEvent) {
        startQuiz(quiz);
    }
}
