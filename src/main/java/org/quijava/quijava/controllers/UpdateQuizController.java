package org.quijava.quijava.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.quijava.quijava.dao.QuizDao;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.services.CategoryService;
import org.quijava.quijava.services.ImageService;
import org.quijava.quijava.services.QuizService;
import org.quijava.quijava.utils.ScreenLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;


@Controller
public class UpdateQuizController implements Initializable {

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ObservableList<String> selectedCategories = FXCollections.observableArrayList();
    private final ApplicationContext applicationContext;
    private final ScreenLoader screenLoader;
    private final QuizService quizService;
    private final ImageService imageService;
    private final CategoryService categoryService;
    private final QuizDao quizDao;
    @FXML
    private TextField titleQuiz;
    @FXML
    private TextArea descriptionQuiz;
    @FXML
    private Button sendImageQuiz;
    @FXML
    private Button back;
    @FXML
    private ImageView imageView;
    @FXML
    private Button createCategory;
    @FXML
    private Button reload;
    @FXML
    private Button createQuiz;
    @FXML
    private ComboBox<String> listCategories;
    @FXML
    private ListView<String> listAddCategories;
    private byte[] imagePath;
    private QuizModel quiz;

    @Autowired
    public UpdateQuizController(ApplicationContext applicationContext, ScreenLoader screenLoader, QuizService quizService, ImageService imageService, CategoryService categoryService, QuizDao quizDao) {
        this.applicationContext = applicationContext;
        this.screenLoader = screenLoader;
        this.quizService = quizService;
        this.imageService = imageService;
        this.categoryService = categoryService;
        this.quizDao = quizDao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        listCategories.setItems(items);
        listAddCategories.setItems(selectedCategories);
    }

    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
        selectedCategories.clear();
        loadQuiz();
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
        if (selectedItem != null) {
            selectedCategories.remove(selectedItem);
        }
    }

    @FXML
    public void goBack() {
        selectedCategories.clear();
        screenLoader.loadMyQuizzes((Stage) back.getScene().getWindow(), applicationContext);
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
        items.addAll(categoryService.getAllCategoriesDescriptions());
    }

    private void loadQuiz() {
        Optional<QuizModel> optionalQuiz = quizDao.findById(quiz.getId());
        if (optionalQuiz.isPresent()) {
            QuizModel quiz = optionalQuiz.get();
            titleQuiz.setText(quiz.getTitle());
            descriptionQuiz.setText(quiz.getDescription());
            selectedCategories.addAll(quiz.getCategories().stream().map(CategoryModel::getDescription).toList());
            if (imagePath != null) {
                Image image = new Image(new ByteArrayInputStream(imagePath));
                imageView.setImage(image);
            }
        } else {
            System.out.println("Quiz not found");
        }
    }

    @FXML
    public void updateQuiz() {
        String title = titleQuiz.getText();
        String description = descriptionQuiz.getText();
        Set<String> selectedCategoriesSet = new HashSet<>(selectedCategories);

        quizService.updateQuiz(quiz.getId(), title, description, selectedCategoriesSet, imagePath);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atualizado");
        alert.setHeaderText(null);
        alert.setContentText("O quiz foi atualizado com sucesso!");
        alert.showAndWait();

        screenLoader.loadMyQuizzes((Stage) createQuiz.getScene().getWindow(), applicationContext);
    }


    @FXML
    private void selectImage() {
        try {
            byte[] imageBytes = imageService.selectImage();
            if (imageBytes != null) {
                imagePath = imageBytes;
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                imageView.setImage(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
