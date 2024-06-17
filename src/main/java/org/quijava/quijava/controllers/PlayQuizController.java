package org.quijava.quijava.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.quijava.quijava.models.OptionsAnswerModel;
import org.quijava.quijava.models.QuestionModel;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.TypeQuestion;
import org.quijava.quijava.services.LoginService;
import org.quijava.quijava.services.QuizService;
import org.quijava.quijava.services.RankService;
import org.quijava.quijava.services.SessionPreferencesService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class PlayQuizController {

    private final QuizService quizService;
    private final SessionPreferencesService sessionPreferencesService;
    private final LoginService loginService;
    private final RankService rankService;
    private final Set<Integer> shownQuestionIndices = new HashSet<>();
    private final ScreenLoader screenLoader;
    private final ApplicationContext applicationContext;
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
    @FXML
    private Label score;
    @FXML
    private Button finish;
    private Timeline timeline;
    private QuizModel quiz;
    private List<QuestionModel> questions;
    private int currentQuestionIndex = 0;
    private javafx.util.Duration totalTimeSpent = javafx.util.Duration.ZERO;
    private long startTime;
    private int totalScore = 0;
    private boolean timeUp = false;

    @Autowired
    public PlayQuizController(QuizService quizService, SessionPreferencesService sessionPreferencesService, LoginService loginService, RankService rankService, ScreenLoader screenLoader, ApplicationContext applicationContext) {
        this.quizService = quizService;
        this.sessionPreferencesService = sessionPreferencesService;
        this.loginService = loginService;
        this.rankService = rankService;
        this.screenLoader = screenLoader;
        this.applicationContext = applicationContext;
    }

    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
        countPlayQuiz();
        loadQuestions();
        setScore();
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
        timeUp = false; // Resetar a variável de tempo ao iniciar o timer
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
        timeUp = true;
        timeline.stop();
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

        for (OptionsAnswerModel answer : optionsAnswers) {
            CheckBox checkBox = new CheckBox(answer.getOptionText());
            checkBox.setUserData(answer);
            checkBoxes.add(checkBox);
            answersField.getChildren().add(checkBox);
        }

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
        if (timeline.getStatus() == Animation.Status.RUNNING || timeUp) {
            boolean answerSelected = false;
            CheckBox selectedCheckBox = null;

            // Verifica se alguma resposta foi selecionada
            for (Node node : answersField.getChildren()) {
                if (node instanceof CheckBox checkBox) {
                    if (checkBox.isSelected()) {
                        answerSelected = true;
                        selectedCheckBox = checkBox;
                        break;
                    }
                }
            }
            OptionsAnswerModel selectedAnswer = answerSelected ? (OptionsAnswerModel) selectedCheckBox.getUserData() : null;
            boolean isCorrect = answerSelected && selectedAnswer.getIsCorrect();

            if (!answerSelected && !timeUp) {
                return;
            } else if (answerSelected && !timeUp) {
                String title = isCorrect ? "Correto!" : "Errado!";
                String message = isCorrect ? "Você acertou a resposta!" : "Você errou a resposta.";
                showAlert(title, message);
            }


        }

        long endTime = System.currentTimeMillis();
        long timeSpentMillis = endTime - startTime;
        totalTimeSpent = totalTimeSpent.add(Duration.millis(timeSpentMillis));

        if (!timeUp) {
            calculateScore();
        }

        if (shownQuestionIndices.size() >= questions.size()) {
            completeQuiz();
            return;
        }

        currentQuestionIndex++;
        showNextQuestion();
        updateProgress();
        startQuestionTimer();
    }

    private void calculateScore() {
        QuestionModel currentQuestion = questions.get(currentQuestionIndex);
        boolean allCorrect = true;

        for (CheckBox checkBox : answersField.getChildren().filtered(node -> node instanceof CheckBox).toArray(new CheckBox[0])) {
            OptionsAnswerModel answer = (OptionsAnswerModel) checkBox.getUserData();
            boolean isSelected = checkBox.isSelected();

            if ((answer.getIsCorrect() && !isSelected) || (!answer.getIsCorrect() && isSelected)) {
                allCorrect = false;
                break;
            }
        }

        if (allCorrect) {
            for (CheckBox checkBox : answersField.getChildren().filtered(node -> node instanceof CheckBox).toArray(new CheckBox[0])) {
                OptionsAnswerModel answer = (OptionsAnswerModel) checkBox.getUserData();
                if (answer.getIsCorrect()) {
                    totalScore += answer.getScore();
                    setScore();
                }
            }
        }
    }


    private void completeQuiz() {
        rankService.saveRanking(quiz, totalScore, totalTimeSpent);
        String message = "Quiz completo!\n";
        message += "Pontuação Total: " + totalScore + " pontos";

        showAlert("Quiz Completo", message);
        reset();
        screenLoader.loadDetailsQuizScreen((Stage) finish.getScene().getWindow(), applicationContext, quiz);
    }

    private void reset() {
        if (timeline != null) {
            timeline.stop();
            timeline.getKeyFrames().clear();
            timeline = null;
        }
        shownQuestionIndices.clear();
        currentQuestionIndex = 0;
        totalScore = 0;
        totalTimeSpent = Duration.ZERO;
        questions = null;
    }

    private void countPlayQuiz() {
            quizService.countPlayQuiz(quiz);
    }

    private void setScore() {
        score.setText(Integer.toString(totalScore));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
