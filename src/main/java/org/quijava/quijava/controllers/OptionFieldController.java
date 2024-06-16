package org.quijava.quijava.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.quijava.quijava.models.OptionsAnswerModel;
import org.quijava.quijava.models.QuestionModel;
import org.quijava.quijava.models.TypeQuestion;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OptionFieldController {

    private final CreateQuestionController parentController;

    public OptionFieldController(CreateQuestionController parentController) {
        this.parentController = parentController;
    }

    public void initializeOptions(VBox optionsContainer, TextField scoreTextField) {
        optionsContainer.getChildren().clear();
        for (int i = 0; i < 4; i++) {
            addAnswerField(optionsContainer, scoreTextField);
        }
    }

    public List<OptionsAnswerModel> createOptionsAnswers(VBox optionsContainer, TextField scoreTextField, QuestionModel selectedQuestion) {
        List<OptionsAnswerModel> optionsAnswers = new LinkedList<>();
        int score = scoreTextField.getText().isEmpty() ? 0 : Integer.parseInt(scoreTextField.getText());

        for (Node child : optionsContainer.getChildren()) {
            if (child instanceof HBox answerBox) {
                TextArea answerTextArea = (TextArea) answerBox.getChildren().get(1);
                CheckBox checkBox = (CheckBox) answerBox.getChildren().get(0);

                String answerText = answerTextArea.getText();
                if (!answerText.isEmpty()) {
                    boolean isCorrect = checkBox.isSelected();
                    int optionScore = isCorrect ? score : 0;

                    OptionsAnswerModel optionAnswer = new OptionsAnswerModel(); // Crie uma nova instância ou utilize seu método apropriado para criar
                    optionAnswer.setOptionText(answerText);
                    optionAnswer.setIsCorrect(isCorrect);
                    optionAnswer.setScore(optionScore);
                    optionAnswer.setQuestion(selectedQuestion); // Associa a opção à pergunta selecionada

                    optionsAnswers.add(optionAnswer);
                }
            }
        }

        return optionsAnswers;
    }

    public void addAnswerField(VBox optionsContainer, TextField scoreTextField) {
        if (optionsContainer.getChildren().size() == 5) {
            System.out.println("Limite máximo de respostas atingido.");
            return;
        }

        HBox answerBox = createAnswerBox(optionsContainer, scoreTextField);
        optionsContainer.getChildren().add(answerBox);
        updateRemoveButtons(optionsContainer);
        updateAddButton(optionsContainer);
    }

    private HBox createAnswerBox(VBox optionsContainer, TextField scoreTextField) {
        HBox answerBox = new HBox(5);
        answerBox.setAlignment(Pos.CENTER);
        TextArea newAnswerField = createAnswerField();
        CheckBox checkBox = createCheckBox();
        Button addButton = createAddButton(optionsContainer, scoreTextField);
        Button removeButton = createRemoveButton(answerBox, optionsContainer, scoreTextField);

        answerBox.setPadding(new Insets(5));
        answerBox.getChildren().addAll(checkBox, newAnswerField, addButton, removeButton);
        return answerBox;
    }

    private TextArea createAnswerField() {
        TextArea answerField = new TextArea();
        answerField.setPrefWidth(300);
        answerField.setPrefHeight(70);
        answerField.setPromptText("Digite a resposta");
        answerField.setWrapText(true);
        return answerField;
    }

    private CheckBox createCheckBox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setOnAction(event -> parentController.updateQuestionType());
        return checkBox;
    }

    private Button createAddButton(VBox optionsContainer, TextField scoreTextField) {
        Button addButton = new Button("+");
        addButton.setOnAction(event -> addAnswerField(optionsContainer, scoreTextField));
        return addButton;
    }

    private Button createRemoveButton(HBox answerBox, VBox optionsContainer, TextField scoreTextField) {
        Button removeButton = new Button("-");
        removeButton.setOnAction(event -> {
            optionsContainer.getChildren().remove(answerBox);
            updateAddButton(optionsContainer);
            updateRemoveButtons(optionsContainer);
        });
        return removeButton;
    }

    private void updateAddButton(VBox optionsContainer) {
        parentController.addItemButton.setDisable(optionsContainer.getChildren().size() > 5);
    }

    private void updateRemoveButtons(VBox optionsContainer) {
        optionsContainer.getChildren().forEach(node -> {
            if (node instanceof HBox) {
                Button removeButton = (Button) ((HBox) node).getChildren().get(3);
                removeButton.setDisable(optionsContainer.getChildren().size() <= 2);
            }
        });
    }

    public void updateSelectedAnswerScore(Set<OptionsAnswerModel> optionsAnswers, TextField scoreTextField) {
        for (OptionsAnswerModel option : optionsAnswers) {
            if (option.getIsCorrect()) {
                option.setScore(Integer.valueOf(scoreTextField.getText()));
                break;
            }
        }
    }

    public void loadQuestionOptions(VBox optionsContainer, List<OptionsAnswerModel> optionsAnswers, TextField scoreTextField) {
        for (OptionsAnswerModel option : optionsAnswers) {
            HBox answerBox = createAnswerBox(optionsContainer, scoreTextField);
            optionsContainer.getChildren().add(answerBox);

            TextArea newAnswerField = (TextArea) answerBox.getChildren().get(1);
            newAnswerField.setText(option.getOptionText());

            CheckBox checkBox = (CheckBox) answerBox.getChildren().get(0);
            checkBox.setSelected(option.getIsCorrect());

            if (option.getIsCorrect()) {
                scoreTextField.setText(String.valueOf(option.getScore()));
            }
        }

        parentController.typeQuestion = (optionsAnswers.stream().filter(OptionsAnswerModel::getIsCorrect).count() > 1)
                ? TypeQuestion.Multipla_escolha
                : TypeQuestion.Escolha_unica;
    }
}