package org.quijava.quijava.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.quijava.quijava.models.*;
import org.quijava.quijava.repositories.CategoryRepository;
import org.quijava.quijava.repositories.QuizRepository;
import org.quijava.quijava.repositories.UserRepository;
import org.quijava.quijava.utils.ScreenLoader;
import org.quijava.quijava.utils.SessionPreferencesService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
    private final Map<String, Integer> categoryMap = new HashMap<>();
    private final UserRepository userRepository;
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



    public CreateQuizController(ApplicationContext applicationContext, CategoryRepository categoryRepository, QuizRepository quizRepository, ScreenLoader screenLoader, UserRepository userRepository) {
        this.applicationContext = applicationContext;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
        this.screenLoader = screenLoader;
        this.userRepository = userRepository;
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


    /**
     * Carrega a lista de categorias criadas, limpa categorias para não empilhar e mapeia descricao e id
     */
    @FXML
    public void loadCategories() {
        List<CategoryModel> categories = categoryRepository.findAll();
        categoryMap.clear();
        for (CategoryModel category : categories) {
            categoryMap.put(category.getDescription(), category.getId());
            items.add(category.getDescription());
        }
    }


    @FXML
    public void createQuiz() {
        String title = titleQuiz.getText();
        String description = descriptionQuiz.getText();
        Set<String> selectedCategories = new HashSet<>(listAddCategories.getItems());

        // Aqui você deve converter as descrições das categorias em IDs
        Set<Integer> categoryIds = selectedCategories.stream()
                .map(categoryMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Procura o usuario pelo id fornecido no preferences
        UserModel author = userRepository.findById(sessionPreferences.getUserId()).orElse(null);

        // Converte os IDs das Categorias Selecionadas para Objetos
        Set<CategoryModel> categories = findCategoriesByIds(categoryIds);

        // Crie e salve o novo quiz aqui
        QuizModel quiz = new QuizModel();
        quiz.setTitle(title);
        quiz.setImageQuiz(imagePath);
        quiz.setDescription(description);
        quiz.setAuthor(author);
        quiz.setCategories(categories);
        quizRepository.save(quiz);

    }


    /**
     * Converter um conjunto de IDs de categorias
     * @param categoryIds
     * @return Conjunto de objetos do Cateogory Model
     */
    public Set<CategoryModel> findCategoriesByIds(Set<Integer> categoryIds) {
        return new HashSet<>(categoryRepository.findByIdIn(new ArrayList<>(categoryIds)));
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
