package org.quijava.quijava.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.SessionPreferencesModel;
import org.quijava.quijava.models.UserModel;
import org.quijava.quijava.repositories.CategoryRepository;
import org.quijava.quijava.repositories.QuizRepository;
import org.quijava.quijava.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    private final SessionPreferencesModel sessionPreferencesModel;
    private final UserRepository userRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, CategoryRepository categoryRepository, SessionPreferencesModel sessionPreferencesModel, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
        this.sessionPreferencesModel = sessionPreferencesModel;
        this.userRepository = userRepository;
    }

    public ObservableList<String> getAllCategoriesDescriptions() {
        List<CategoryModel> categories = categoryRepository.findAll();
        return FXCollections.observableArrayList(
                categories.stream()
                        .map(CategoryModel::getDescription)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Converter um conjunto de IDs de categorias
     * @param categoryIds
     * @return Conjunto de objetos do Cateogory Model
     */
    public Set<CategoryModel> findCategoriesByIds(Set<Integer> categoryIds) {
        return new HashSet<>(categoryRepository.findByIdIn(new ArrayList<>(categoryIds)));
    }

    public QuizModel createQuiz(String title, String description, Set<String> selectedCategories, byte[] image) {
        Set<Integer> categoryIds = selectedCategories.stream()
                .map(categoryRepository::findByDescription)
                .filter(Objects::nonNull)
                .map(CategoryModel::getId)
                .collect(Collectors.toSet());

        Set<CategoryModel> categories = findCategoriesByIds(categoryIds);
        UserModel author = getLoggedInUser();

        QuizModel quiz = new QuizModel();
        quiz.setTitle(title);
        quiz.setImageQuiz(image);
        quiz.setDescription(description);
        quiz.setAuthor(author);
        quiz.setCategories(categories);
        return quizRepository.save(quiz);
    }

    private UserModel getLoggedInUser() {

        int userId = sessionPreferencesModel.getUserId();

        return userRepository.findById(userId).orElse(null);
    }
}
