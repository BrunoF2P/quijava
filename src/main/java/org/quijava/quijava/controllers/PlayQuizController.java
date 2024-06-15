package org.quijava.quijava.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.quijava.quijava.models.OptionsAnswerModel;
import org.quijava.quijava.models.QuestionModel;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.TypeQuestion;
import org.quijava.quijava.services.LoginService;
import org.quijava.quijava.services.QuizService;
import org.quijava.quijava.services.RankService;
import org.quijava.quijava.services.SessionPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class PlayQuizController {


    private final QuizService quizService;
    private final SessionPreferencesService sessionPreferencesService;
    private final LoginService loginService;
    private final RankService rankService;
    private final Set<Integer> shownQuestionIndices = new HashSet<>();
    @FXML
    private Text questionText;
    @FXML
    private Text progressText;
    @FXML
    private Text timeText;
    @FXML
    private Text typeQuestion;
    @FXML
    private VBox answersField;
    @FXML
    private ProgressBar timeProgressBar;
    private Timeline timeline;
    private QuizModel quiz;
    private List<QuestionModel> questions;
    private int currentQuestionIndex = 0;
    private javafx.util.Duration totalTimeSpent = javafx.util.Duration.ZERO;
    private long startTime;
    private int totalScore = 0;

    @Autowired
    public PlayQuizController(QuizService quizService, SessionPreferencesService sessionPreferencesService, LoginService loginService, RankService rankService) {
        this.quizService = quizService;
        this.sessionPreferencesService = sessionPreferencesService;
        this.loginService = loginService;
        this.rankService = rankService;
    }


    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
        loadQuestions();
        showNextQuestion();
        updateProgress();
        startQuestionTimer();
    }

    private void loadQuestions() {
        questions = quizService.getAllQuestionsByQuizId(quiz.getId());
        shownQuestionIndices.clear();
    }

    private void startQuestionTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        startTime = System.currentTimeMillis();
        QuestionModel currentQuestion = questions.get(currentQuestionIndex);
        Duration questionTimeLimit = Duration.millis(currentQuestion.getLimiteTime().toMillis());
        double totalTimeSeconds = questionTimeLimit.toSeconds();

        final double[] remainingTime = {totalTimeSeconds};

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            // Atualizando o tempo restante
            remainingTime[0]--;
            if (remainingTime[0] <= 0) {
                handleTimeUp();
            }
            // Atualizando o progresso da barra de progresso
            double progress = remainingTime[0] / questionTimeLimit.toSeconds();
            timeProgressBar.setProgress(progress);

            int minutes = (int) (remainingTime[0] / 60);
            int seconds = (int) (remainingTime[0] % 60);
            timeText.setText(String.format("%02d:%02d", minutes, seconds));
        }));
        timeline.setCycleCount((int) totalTimeSeconds);
        timeline.play();
    }


    private void handleTimeUp() {
        System.out.println("Tempo esgotado para a pergunta atual!");
        onNextQuestion();
    }

    private void updateProgress() {
        progressText.setText("Pergunta " + (currentQuestionIndex + 1) + "/" + questions.size());
    }


    private void showNextQuestion() {
        int nextIndex = getNextQuestionIndex();
        QuestionModel currentQuestion = questions.get(nextIndex);
        shownQuestionIndices.add(nextIndex); // Adiciona o índice da pergunta exibida
        questionText.setText(currentQuestion.getQuestionText());
        typeQuestion.setText(currentQuestion.getTypeQuestion().toString().replace("_", " "));
        showAnswers(currentQuestion);
    }

    private int getNextQuestionIndex() {
        int nextIndex;
        do {
            // Gera um índice aleatório até encontrar um não exibido
            nextIndex = (int) (Math.random() * questions.size());
        } while (shownQuestionIndices.contains(nextIndex)); // Verifica se o índice já foi exibido
        return nextIndex;
    }

    private void showAnswers(QuestionModel question) {

        answersField.getChildren().clear();

        List<CheckBox> checkBoxes = new ArrayList<>();

        List<OptionsAnswerModel> optionsAnswers = new ArrayList<>(question.getOptionsAnswers());

        Collections.shuffle(optionsAnswers);

        question.getOptionsAnswers().forEach(answer -> {
            CheckBox checkBox = new CheckBox(answer.getOptionText());
            checkBox.setUserData(answer.getIsCorrect());
            checkBoxes.add(checkBox);
            answersField.getChildren().add(checkBox);
        });

        if (question.getTypeQuestion() == TypeQuestion.Escolha_unica) {
            checkBoxes.forEach(checkBox -> checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    checkBoxes.forEach(cb -> {
                        if (cb != checkBox) {
                            cb.setSelected(false);
                        }
                    });
                }
            }));
        }
    }

    @FXML
    private void onNextQuestion() {
        long endTime = System.currentTimeMillis();
        long timeSpentMillis = endTime - startTime;
        calculateScoreForCurrentQuestion();
        totalTimeSpent = totalTimeSpent.add(javafx.util.Duration.millis(timeSpentMillis));
        if (shownQuestionIndices.size() >= questions.size()) {
            completeQuiz();
            return;
        }

        currentQuestionIndex++;
        showNextQuestion();
        updateProgress();
        startQuestionTimer();
    }

    private void calculateScoreForCurrentQuestion() {
        QuestionModel currentQuestion = questions.get(currentQuestionIndex);
        List<OptionsAnswerModel> optionsList = new ArrayList<>(currentQuestion.getOptionsAnswers());
        int questionScore = 0;
        for (int i = 0; i < optionsList.size(); i++) {
            CheckBox checkBox = (CheckBox) answersField.getChildren().get(i);
            boolean selected = checkBox.isSelected();
            OptionsAnswerModel option = optionsList.get(i);
            if (selected && option.getIsCorrect()) {
                questionScore += option.getScore();
            } else if (!selected && !option.getIsCorrect()) {
                questionScore += option.getScore();
            }
        }
        totalScore += questionScore;
    }


    private void completeQuiz() {
        rankService.saveRanking(quiz, totalScore, totalTimeSpent);
    }

}
