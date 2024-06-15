package org.quijava.quijava.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.quijava.quijava.models.QuizModel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class ListQuizView {

    public VBox createQuizBox(QuizModel quiz, Runnable startQuiz, Runnable showDetails) {
        VBox quizBox = new VBox();
        quizBox.setAlignment(Pos.TOP_CENTER);
        quizBox.setSpacing(20);
        quizBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10px; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px;");
        quizBox.setOnMouseClicked(event -> showDetails.run()); // Adiciona evento de clique no VBox

        Text descriptionText = new Text(quiz.getTitle());
        descriptionText.setFont(Font.font(18));
        VBox.setMargin(descriptionText, new Insets(20.0, 0.0, 0.0, 0.0));

        ImageView imageView = new ImageView();
        Image image = bytesToImage(quiz.getImageQuiz());
        imageView.setImage(image);
        imageView.setFitWidth(450);
        imageView.setFitHeight(200);

        ButtonBar buttonBar = new ButtonBar();

        Button playButton = new Button("Jogar");
        playButton.setStyle("-fx-background-color: #2196f3;");
        buttonBar.getButtons().addAll(playButton);
        VBox.setMargin(buttonBar, new Insets(0, 0, 20, 0));

        playButton.setOnAction(event -> startQuiz.run());
        quizBox.getChildren().addAll(descriptionText, imageView, buttonBar);

        return quizBox;
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
