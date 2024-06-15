package org.quijava.quijava.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import org.quijava.quijava.models.RankingModel;

public class RankingCellFactory implements Callback<ListView<RankingModel>, ListCell<RankingModel>> {

    @Override
    public ListCell<RankingModel> call(ListView<RankingModel> param) {
        return new ListCell<RankingModel>() {
            private final HBox hbox;
            private final Circle circle;
            private final Rectangle separator;
            private final VBox vbox;
            private final Label orderLabel;
            private final Label nameLabel;
            private final Label scoreLabel;
            private final Label timeLabel;

            {
                hbox = new HBox();
                circle = new Circle(10);
                circle.setFill(Color.LIGHTBLUE);

                separator = new Rectangle(1, 60);
                separator.setFill(Color.LIGHTGRAY);

                orderLabel = new Label();
                orderLabel.setStyle("-fx-font-weight: bold;");

                vbox = new VBox();
                nameLabel = new Label();
                nameLabel.setStyle("-fx-font-weight: bold;");
                scoreLabel = new Label();
                timeLabel = new Label();

                vbox.getChildren().addAll(nameLabel, scoreLabel, timeLabel);
                vbox.setSpacing(3);

                hbox.getChildren().addAll(circle, orderLabel, separator, vbox);
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(vbox, Priority.ALWAYS);
                hbox.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(RankingModel item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    int index = getIndex() + 1;
                    orderLabel.setText(String.valueOf(index));

                    nameLabel.setText("Jogador: " + item.getUser().getUsername());
                    nameLabel.setStyle("-fx-font-size: 14px;");

                    scoreLabel.setText("Pontos: " + item.getTotalScore());
                    scoreLabel.setStyle("-fx-font-size: 12px;");

                    long totalTimeSeconds = item.getTotalTime().getSeconds();
                    long minutes = totalTimeSeconds / 60;
                    long seconds = totalTimeSeconds % 60;
                    timeLabel.setText(String.format("Tempo: %02d:%02d", minutes, seconds));
                    timeLabel.setStyle("-fx-font-size: 12px;");

                    setGraphic(hbox);
                }
            }
        };
    }
}
