package org.quijava.quijava.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.quijava.quijava.models.QuizModel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Component
public class MyListQuizView {

    public VBox createQuizBox(QuizModel quiz, Runnable onDelete, Runnable editQuiz, Runnable startQuiz, Runnable showDetails) {
        VBox quizBox = new VBox();
        quizBox.setAlignment(Pos.TOP_CENTER);
        quizBox.setSpacing(20);
        quizBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10px; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px;");
        quizBox.setOnMouseClicked(event -> showDetails.run()); // Adiciona evento de clique no VBox
        quizBox.setMinWidth(360);

        Text descriptionText = new Text(quiz.getTitle());
        descriptionText.setFont(Font.font(18));
        VBox.setMargin(descriptionText, new Insets(20.0, 0.0, 0.0, 0.0));

        ImageView imageView = new ImageView();
        Image image = bytesToImage(quiz.getImageQuiz());
        imageView.setImage(image);
        imageView.setFitWidth(360);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        ButtonBar buttonBar = new ButtonBar();
        Button playing = new Button("Jogado " + quiz.getTotalAttempts().toString());
        playing.setDisable(true);
        Button deleteButton = new Button("Deletar");
        Button editButton = new Button("Editar");
        Button playButton = new Button("Jogar");
        deleteButton.setStyle("-fx-background-color: #ff5757;");
        editButton.setStyle("-fx-background-color: #4caf50;");
        playButton.setStyle("-fx-background-color: #2196f3;");
        buttonBar.getButtons().addAll(playing, deleteButton, editButton, playButton);
        VBox.setMargin(buttonBar, new Insets(0, 0, 20, 0));

        deleteButton.setOnAction(event -> showDeleteConfirmation(quiz, onDelete));
        editButton.setOnAction(event -> editQuiz.run());
        playButton.setOnAction(event -> startQuiz.run());
        quizBox.getChildren().addAll(descriptionText, imageView, buttonBar);

        return quizBox;
    }

    private void showDeleteConfirmation(QuizModel quiz, Runnable onDelete) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Exclusão");
        alert.setHeaderText("Você está prestes a deletar o quiz: " + quiz.getTitle());
        alert.setContentText("Tem certeza que deseja continuar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onDelete.run();
        }
    }

    private Image bytesToImage(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            return new Image(new ByteArrayInputStream(bytes));
        } else {
            return getDefaultImage();
        }
    }

    private Image getDefaultImage() {
        return new Image(getClass().getResourceAsStream("/images/logo.png"));
    }
}
