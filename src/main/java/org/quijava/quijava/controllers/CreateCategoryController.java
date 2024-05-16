package org.quijava.quijava.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.repositories.CategoryRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@Component
@ComponentScan
public class CreateCategoryController {

    private final CategoryRepository categoryRepository;

    @FXML
    private Button createCategory;

    @FXML
    private TextField categoryNameField;

    @FXML
    private Label alert;

    public CreateCategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @FXML
    void cadastrar(ActionEvent event) {
        String category_name = categoryNameField.getText();

        if (categoryValidation(category_name)) {
            createCategory(category_name);
        }
    }


    private boolean categoryValidation(String category_name){

        if (category_name.isEmpty()) {  // verifica se os campos de username e password estão vazios
            setAlert("Preencha todos os campos.");
            return false;
        }

        if (!category_name.matches("^[a-zA-Z0-9]+$")) {  // verifica se o username so possui letras e numeros
            setAlert("As categorias deve conter apenas letras e números");
            return false;
        }

        if (categoryRepository.existsByDescription(category_name)) { // verifica se o usuario existe
            setAlert("Categoria já cadastrada.");
            return false;
        }

        return true;
    }

    private void setAlert(String message) {
        alert.setText(message);
    }

    private void createCategory(String category_name){
        CategoryModel newCategory = new CategoryModel();
        newCategory.setDescription(category_name);
        categoryRepository.save(newCategory);
    }
}
