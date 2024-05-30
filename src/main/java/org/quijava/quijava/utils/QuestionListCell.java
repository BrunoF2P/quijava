package org.quijava.quijava.utils;

import javafx.scene.control.ListCell;
import org.quijava.quijava.models.QuestionModel;

public class QuestionListCell extends ListCell<QuestionModel> {

    @Override
    protected void updateItem(QuestionModel question, boolean empty) {
        super.updateItem(question, empty);

        if (empty || question == null) {
            setText(null);
        } else {
            setText(question.getQuestionText());
        }
    }
}


