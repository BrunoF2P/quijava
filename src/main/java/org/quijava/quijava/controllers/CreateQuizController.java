package org.quijava.quijava.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.QuizzesCategory;
import org.quijava.quijava.models.QuizzesCategoryId;
import org.quijava.quijava.repositories.CategoryRepository;
import org.quijava.quijava.repositories.QuizRepository;
import org.quijava.quijava.repositories.QuizzesCategoryRepository;
import org.quijava.quijava.utils.ScreenLoader;
import org.quijava.quijava.utils.SessionPreferencesService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import javafx.fxml.Initializable;


@ComponentScan
@Component
public class CreateQuizController implements Initializable{

    private final ApplicationContext applicationContext;
    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    private final ScreenLoader screenLoader;
    private Image selectedImage;
    private final QuizModel quiz;
    private final QuizzesCategoryRepository quizzesCategoryRepository;

    SessionPreferencesService sessionPreferences= new SessionPreferencesService();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        listCategories.setItems(items);
        listAddCategories.setItems(selectedCategories);
    }



    public CreateQuizController(ApplicationContext applicationContext, CategoryRepository categoryRepository, QuizRepository quizRepository, ScreenLoader screenLoader, QuizzesCategoryRepository quizzesCategoryRepository) {
        this.applicationContext = applicationContext;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
        this.screenLoader = screenLoader;
        this.quizzesCategoryRepository = quizzesCategoryRepository;
        this.quiz = new QuizModel();
    }




    @FXML
    public void selectCategories(){
        String selectedItem = listCategories.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedCategories.contains(selectedItem)) {
            selectedCategories.add(selectedItem);
        }
    }

    @FXML
    public void deleteSelectedCategories(){
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

    @FXML
    public void loadCategories() {
        List<CategoryModel> categories = categoryRepository.findAll();
        items.clear();
        items.addAll(categories.stream().map(CategoryModel::getDescription).toList());
    }


    @FXML
    public void createQuiz() {
        String title = titleQuiz.getText();
        String description = descriptionQuiz.getText();
        Set<String> selectedCategories = new HashSet<>(listAddCategories.getItems());

        // Aqui você deve converter as descrições das categorias em IDs
        Set<Integer> categoryIds = selectedCategories.stream()
                .map(this::findCategoryIdByDescription)
                .collect(Collectors.toSet());

        Set<CategoryModel> categories = findCategoriesByIds(categoryIds);

        // Crie e salve o novo quiz aqui
        QuizModel quiz = new QuizModel();
        quiz.setTitle(title);
        quiz.setImageQuiz(imagePath);
        quiz.setDescription(description);
        quiz.setCategories(categories);
        quiz.setAuthorId(sessionPreferences.getUserId());

        QuizModel savedQuiz = quizRepository.save(quiz); // Salva o quiz e obtém o ID

        for (CategoryModel category : categories) {
            QuizzesCategory quizzesCategory = new QuizzesCategory();
            quizzesCategory.setId(new QuizzesCategoryId(savedQuiz.getId(), category.getId()));
            quizzesCategory.setQuiz(savedQuiz);
            quizzesCategory.setCategory(category);
            quizzesCategoryRepository.save(quizzesCategory); // Salva a associação na tabela de junção
        }

    }

    public Integer findCategoryIdByDescription(String description) {
        CategoryModel category = categoryRepository.findByDescription(description);
        return category != null ? category.getId() : null;
    }

    public Set<CategoryModel> findCategoriesByIds(Set<Integer> categoryIds) {
        List<CategoryModel> categoryModels = categoryRepository.findByIdIn(new ArrayList<>(categoryIds));
        return new HashSet<>(categoryModels);
    }


    @FXML
    public void selectImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha a imagem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tipos de arquivos", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                imagePath = Files.readAllBytes(selectedFile.toPath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
