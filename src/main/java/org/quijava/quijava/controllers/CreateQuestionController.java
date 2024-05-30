package org.quijava.quijava.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.quijava.quijava.models.*;
import org.quijava.quijava.services.ImageService;
import org.quijava.quijava.services.QuestionService;
import org.quijava.quijava.utils.QuestionListCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class CreateQuestionController {

    @FXML
    private VBox optionsContainer;

    @FXML
    private ListView<QuestionModel> questionListView;

    @FXML
    Button addItemButton;

    @FXML
    private TextArea questionText;

    @FXML
    private ChoiceBox<QuestionDifficulty> difficultyChoiceBox;

    @FXML
    private TextField durationTextField;

    @FXML
    private TextField scoreTextField;

    @FXML
    private Button sendImageQuestion;

    private QuizModel quiz;

    private QuestionModel currentQuestion;

    TypeQuestion typeQuestion;

    private byte[] imagePath;

    private final ObservableList<QuestionModel> temporaryQuestions = FXCollections.observableArrayList();
    private final ObservableList<OptionsAnswerModel> temporaryAnswers =  FXCollections.observableArrayList();

    private final QuestionService questionService;
    private final OptionFieldController optionFieldController;
    private final ImageService imageService;

    @Autowired
    public CreateQuestionController(QuestionService questionService, ImageService imageService) {
        this.questionService = questionService;
        this.imageService = imageService;
        this.optionFieldController = new OptionFieldController(this);
    }

    public void setQuizModel(QuizModel quizModel) {
        this.quiz = quizModel;
    }

    public void initialize() {
        setupListView();
        setupChoiceBox();
        optionFieldController.initializeOptions(optionsContainer, scoreTextField);
    }

    private void setupListView() {
        questionListView.setCellFactory(param -> new QuestionListCell());
        refreshListView();
    }

    private void setupChoiceBox() {
        difficultyChoiceBox.setItems(FXCollections.observableArrayList(QuestionDifficulty.values()));
        difficultyChoiceBox.setValue(QuestionDifficulty.FACIL);
    }

    @FXML
    public void addItem() {
        Set<OptionsAnswerModel> optionsAnswers = optionFieldController.createOptionsAnswers(optionsContainer, scoreTextField, questionService);
        temporaryAnswers.addAll(optionsAnswers);

        QuestionModel newQuestion = questionService.createQuestion(
                questionText.getText(),
                quiz,
                typeQuestion,
                durationTextField.getText(),
                optionsAnswers,
                difficultyChoiceBox.getValue(),
                imagePath
        );

        temporaryQuestions.add(newQuestion);
        clearFields();
        refreshListView();
    }

    @FXML
    public void createQuestion() {
        if (!temporaryQuestions.isEmpty()) {
            for (QuestionModel question : temporaryQuestions) {
                Set<OptionsAnswerModel> optionsAnswers = new HashSet<>();
                for (OptionsAnswerModel answer : temporaryAnswers) {
                    if (answer.getQuestion() == question) {
                        optionsAnswers.add(answer);
                    }
                }

                questionService.saveQuestions(Collections.singleton(question));
                questionService.saveOptionsAnswers(optionsAnswers);
            }

            temporaryQuestions.clear();
            refreshListView();
            clearFields();
        }
    }

    @FXML
    public void editList() {
        QuestionModel selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            currentQuestion = selectedQuestion;
            loadQuestionDetails(selectedQuestion);
        }
    }

    @FXML
    private void deleteQuestion() {
        QuestionModel selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            temporaryQuestions.remove(selectedQuestion);
            refreshListView();
        }
    }

    @FXML
    private void updateQuestion() {
        QuestionModel selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            selectedQuestion.setQuestionText(questionText.getText());
            selectedQuestion.setQuestionDifficulty(difficultyChoiceBox.getValue());

            Set<OptionsAnswerModel> updatedOptionsAnswers = optionFieldController.createOptionsAnswers(optionsContainer, scoreTextField, questionService);
            selectedQuestion.getOptionsAnswers().clear();
            selectedQuestion.getOptionsAnswers().addAll(updatedOptionsAnswers);

            optionFieldController.updateSelectedAnswerScore(updatedOptionsAnswers, scoreTextField);

            if (imagePath != null) {
                selectedQuestion.setImageQuestion(imagePath);
            }

            refreshListView();
            clearFields();
            questionListView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void selectImage() {
        try {
            imagePath = imageService.selectImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshListView() {
        questionListView.setItems(FXCollections.observableArrayList(temporaryQuestions));
    }

    private void loadQuestionDetails(QuestionModel question) {
        questionText.setText(question.getQuestionText());
        optionsContainer.getChildren().clear();
        difficultyChoiceBox.setValue(question.getQuestionDifficulty());
        Duration duration = Duration.parse(question.getLimiteTime().toString());
        int limiteTimeInSeconds = (int) duration.getSeconds();
        durationTextField.setText(String.valueOf(limiteTimeInSeconds));

        optionFieldController.loadQuestionOptions(optionsContainer, question.getOptionsAnswers(), scoreTextField);

        imagePath = question.getImageQuestion();
        if (imagePath != null) {
            System.out.println("Imagem carregada com sucesso.");
        }
    }

    private void clearFields() {
        difficultyChoiceBox.setValue(QuestionDifficulty.FACIL);
        durationTextField.clear();
        scoreTextField.clear();
        questionText.clear();
        imagePath = null;
        optionFieldController.initializeOptions(optionsContainer, scoreTextField);
    }

    void updateQuestionType() {
        int selectedCount = countSelectedCheckBoxes();
        typeQuestion = (selectedCount == 1) ? TypeQuestion.ESCOLHA_UNICA : TypeQuestion.ESCOLHA_MULTIPLA;
    }

    private int countSelectedCheckBoxes() {
        int count = 0;
        for (Node child : optionsContainer.getChildren()) {
            if (child instanceof HBox) {
                CheckBox checkBox = (CheckBox) ((HBox) child).getChildren().get(0);
                if (checkBox.isSelected()) {
                    count++;
                }
            }
        }
        return count;
    }


}
