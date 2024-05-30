package org.quijava.quijava.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.quijava.quijava.models.*;
import org.quijava.quijava.services.ImageService;
import org.quijava.quijava.services.QuizService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.context.ApplicationContext;


import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.fxml.Initializable;
import org.springframework.stereotype.Controller;


@Controller
public class CreateQuizController implements Initializable{

    private final ApplicationContext applicationContext;
    private final ScreenLoader screenLoader;
    private final QuizService quizService;
    private final ImageService imageService;

    @FXML
    private TextField titleQuiz;

    @FXML
    private TextArea descriptionQuiz;

    @FXML
    private Button sendImageQuiz;

    @FXML
    private Button back;

    @FXML
    private Button createCategory;

    @FXML
    private  Button reload;

    @FXML
    private Button createQuiz;

    @FXML
    private ComboBox<String> listCategories;

    @FXML
    private ListView<String> listAddCategories;

    private byte[] imagePath;

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ObservableList<String> selectedCategories = FXCollections.observableArrayList();


    public CreateQuizController(ApplicationContext applicationContext, ScreenLoader screenLoader, QuizService quizService, ImageService imageService) {
        this.applicationContext = applicationContext;
        this.screenLoader = screenLoader;
        this.quizService = quizService;
        this.imageService = imageService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        listCategories.setItems(items);
        listAddCategories.setItems(selectedCategories);
    }

    @FXML
    public void selectCategories() {
        String selectedItem = listCategories.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedCategories.contains(selectedItem)) {
            selectedCategories.add(selectedItem);
        }
    }

    @FXML
    public void deleteSelectedCategories() {
        String selectedItem = listAddCategories.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedCategories.contains(selectedItem)) {
            selectedCategories.remove(selectedItem);
        }
    }

    @FXML
    public void goBack() {
        selectedCategories.clear();
        screenLoader.loadMenuScreen((Stage) back.getScene().getWindow(), applicationContext);
    }

    @FXML
    public void createCategory() {
        screenLoader.loadCreateCategoryScreen(applicationContext);
    }


    /**
     * Carrega a lista de categorias criadas, limpa categorias para não empilhar e mapeia descricao e id
     */
    @FXML
    public void loadCategories() {
        items.clear();
        items.addAll(quizService.getAllCategoriesDescriptions());
    }


    @FXML
    public void createQuiz() {
        String title = titleQuiz.getText();
        String description = descriptionQuiz.getText();

        Set<String> selectedCategoriesSet = new HashSet<>(selectedCategories);
        QuizModel quiz = quizService.createQuiz(title, description, selectedCategoriesSet, imagePath);

        screenLoader.loadCreateQuestionScreen((Stage) createQuiz.getScene().getWindow(), applicationContext, quiz);
    }


    @FXML
    public void selectImage() {
        try {
            imagePath = imageService.selectImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
