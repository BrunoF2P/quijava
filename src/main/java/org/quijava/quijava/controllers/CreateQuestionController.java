package org.quijava.quijava.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.quijava.quijava.models.*;
import org.quijava.quijava.services.ImageService;
import org.quijava.quijava.services.QuestionService;
import org.quijava.quijava.utils.QuestionListCell;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CreateQuestionController {

    private final ObservableList<QuestionModel> questions = FXCollections.observableArrayList();
    private final ObservableList<OptionsAnswerModel> answers = FXCollections.observableArrayList();
    private final QuestionService questionService;
    private final OptionFieldController optionFieldController;
    private final ImageService imageService;
    private final ScreenLoader screenLoader;
    private final ApplicationContext applicationContext;
    @FXML
    Button addItemButton;
    TypeQuestion typeQuestion;
    @FXML
    private VBox optionsContainer;
    @FXML
    private ListView<QuestionModel> questionListView;
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
    @FXML
    private Button finishButton;
    private QuizModel quiz;
    private QuestionModel currentQuestion;
    private byte[] imagePath;

    @Autowired
    public CreateQuestionController(QuestionService questionService, ImageService imageService, ScreenLoader screenLoader, ApplicationContext applicationContext) {
        this.questionService = questionService;
        this.imageService = imageService;
        this.screenLoader = screenLoader;
        this.applicationContext = applicationContext;
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
        // Criar e salvar a pergunta
        QuestionModel newQuestion = questionService.createQuestion(
                questionText.getText(),
                quiz,
                typeQuestion,
                durationTextField.getText(),
                Collections.emptyList(), // Inicialmente sem respostas associadas
                difficultyChoiceBox.getValue(),
                imagePath
        );

        // Associar respostas à pergunta
        List<OptionsAnswerModel> optionsAnswers = optionFieldController.createOptionsAnswers(optionsContainer, scoreTextField, newQuestion);
        for (OptionsAnswerModel answer : optionsAnswers) {
            answer.setQuestion(newQuestion); // Associar cada resposta à nova pergunta
        }

        // Salvar a pergunta e as respostas
        questionService.saveQuestion(newQuestion); // Salvar a pergunta
        questionService.saveOptionsAnswers(optionsAnswers);// Salvar as respostas associadas

        questions.add(newQuestion);

        clearFields();
        refreshListView();
    }

    @FXML
    private void createQuestion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Concluido");
        alert.setHeaderText(null);
        alert.setContentText("Sucessso!");
        alert.showAndWait();

        screenLoader.loadMyQuizzes((Stage) finishButton.getScene().getWindow(), applicationContext);

}

    @FXML
    private void editList() {
        QuestionModel selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            // Aqui você pode usar o ID da pergunta para buscar os detalhes completos no banco de dados
            Optional<QuestionModel> optionalQuestion = questionService.findById(selectedQuestion.getId());
            if (optionalQuestion.isPresent()) {
                QuestionModel question = optionalQuestion.get();
                loadQuestionDetails(question); // Carrega os detalhes da pergunta na interface gráfica
            } else {
                // Tratar cenário onde a pergunta não é encontrada
                System.out.println("Pergunta não encontrada");
            }
        }
    }

    @FXML
    private void deleteQuestion() {
        QuestionModel selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            try {
                questionService.deleteQuestion(selectedQuestion.getId());
                questions.remove(selectedQuestion);
                refreshListView();
            } catch (Exception e) {
                // Handle exceptions appropriately
                e.printStackTrace();
            }
        }

        clearFields();
        refreshListView();
        questionListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void updateQuestion() {
        QuestionModel selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            selectedQuestion.setQuestionText(questionText.getText());
            selectedQuestion.setQuestionDifficulty(difficultyChoiceBox.getValue());

            try {
                long durationSeconds = Long.parseLong(durationTextField.getText());
                Duration duration = Duration.ofSeconds(durationSeconds);
                selectedQuestion.setLimiteTime(duration);
            } catch (NumberFormatException e) {
                System.out.println("Erro ao converter a duração para segundos.");
                return;
            }

            List<OptionsAnswerModel> updatedOptionsAnswers = optionFieldController.createOptionsAnswers(optionsContainer, scoreTextField, selectedQuestion);
            selectedQuestion.setOptionsAnswers(updatedOptionsAnswers); // Atualiza todas as opções de resposta de uma vez

            questionService.updateQuestion(selectedQuestion);

            clearFields();
            refreshListView();
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
        questionListView.setItems(FXCollections.observableArrayList(questions));
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


    void updateQuestionType() {
        int selectedCount = countSelectedCheckBoxes();
        typeQuestion = (selectedCount == 1) ? TypeQuestion.Escolha_unica : TypeQuestion.Multipla_escolha;
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

    private void clearFields() {
        difficultyChoiceBox.setValue(QuestionDifficulty.FACIL);
        durationTextField.clear();
        scoreTextField.clear();
        questionText.clear();
        imagePath = null;
        optionFieldController.initializeOptions(optionsContainer, scoreTextField);
    }

}
