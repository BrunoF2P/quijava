package org.quijava.quijava.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.quijava.quijava.services.CategoryService;
import org.springframework.stereotype.Controller;


@Controller
public class CreateCategoryController {

    private final CategoryService categoryService;

    @FXML
    private Button createCategory;

    @FXML
    private TextField categoryNameField;

    @FXML
    private Label alert;

    public CreateCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @FXML
    void cadastrar(ActionEvent event) {
        String categoryName = categoryNameField.getText();
        try {
            categoryService.createCategory(categoryName);
        } catch (IllegalArgumentException e) {
            setAlert(e.getMessage());
        } catch (Exception e) {
            setAlert("Erro ao cadastrar a categoria");
            e.printStackTrace();
        }
    }

    @FXML
    private void setAlert(String message) {
        alert.setText(message);
    }
}
