package org.quijava.quijava.services;

import org.quijava.quijava.dao.*;
import org.quijava.quijava.models.CategoryModel;
import org.quijava.quijava.models.QuestionModel;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.UserModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    private final QuizDao quizDao;
    private final CategoryDao categoryDao;
    private final SessionPreferencesService sessionPreferences;
    private final UserDao userDao;
    private final CategoryService categoryService;
    private final OptionsAnswerDao optionsAnswerDao;
    private final QuestionDao questionDao;

    public QuizService(QuizDao quizDao,
                       UserDao userDao,
                       SessionPreferencesService sessionPreferences,
                       CategoryDao categoryDao,
                       CategoryService categoryService,
                       OptionsAnswerDao optionsAnswerDao,
                       QuestionDao questionDao) {
        this.quizDao = quizDao;
        this.categoryDao = categoryDao;
        this.sessionPreferences = sessionPreferences;
        this.userDao = userDao;
        this.categoryService = categoryService;
        this.optionsAnswerDao = optionsAnswerDao;
        this.questionDao = questionDao;
    }

    public QuizModel createQuiz(String title, String description, Set<String> selectedCategories, byte[] image) {
        Set<Integer> categoryIds = selectedCategories.stream()
                .map(categoryDao::findByDescription)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(CategoryModel::getId)
                .collect(Collectors.toSet());

        Set<CategoryModel> categories = categoryService.findCategoriesByIds(categoryIds);
        UserModel author = getLoggedInUser();

        QuizModel quiz = new QuizModel();
        quiz.setTitle(title);
        quiz.setImageQuiz(image);
        quiz.setDescription(description);
        quiz.setAuthor(author);
        quiz.setCategories(categories);

        return quizDao.save(quiz);
    }

    public List<QuestionModel> getAllQuestionsByQuizId(Integer quizId) {
        return questionDao.findByQuizIdWithOptions(quizId);
    }

    public List<QuizModel> findAllQuizzesByUserId(Integer userId) {
        return quizDao.findByAuthorId(userId);
    }

    public void deleteQuiz(Integer quizId) {
        optionsAnswerDao.deleteByQuizId(quizId);
        questionDao.deleteByQuizId(quizId);
        quizDao.deleteById(quizId);
    }

    public QuizModel updateCategoryQuiz(Integer quizId, Set<String> newCategories) {
        QuizModel quiz = quizDao.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz não encontrado"));

        Set<Integer> categoryIds = newCategories.stream()
                .map(categoryDao::findByDescription)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(CategoryModel::getId)
                .collect(Collectors.toSet());

        Set<CategoryModel> categories = categoryService.findCategoriesByIds(categoryIds);
        quiz.setCategories(categories);

        return quizDao.save(quiz);
    }

    public QuizModel updateQuiz(Integer id, String title, String description, Set<String> newCategories, byte[] image) {
        QuizModel quiz = quizDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz não encontrado"));

        Set<Integer> categoryIds = newCategories.stream()
                .map(categoryDao::findByDescription)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(CategoryModel::getId)
                .collect(Collectors.toSet());

        Set<CategoryModel> categories = categoryService.findCategoriesByIds(categoryIds);

        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setCategories(categories);

        if (image != null && image.length > 0) {
            quiz.setImageQuiz(image);
        }

        return quizDao.save(quiz);
    }

    public List<QuizModel> findQuizzesByCategory(Integer categoryId) {
        return quizDao.findByCategoriesId(categoryId);
    }

    public void countPlayQuiz(QuizModel quiz) {
        quiz.setTotalAttempts(quiz.getTotalAttempts() + 1);
        quizDao.save(quiz);
    }

    public Optional<QuizModel> findById(Integer quizId) {
        return quizDao.findById(quizId);
    }

    public Optional<QuizModel> findByIdWithCategories(Integer quizId) {
        return quizDao.findByIdWithCategories(quizId);
    }

    private UserModel getLoggedInUser() {
        return sessionPreferences.getUserId()
                .flatMap(userDao::findById)
                .orElseThrow(() -> new IllegalStateException("Usuário não está logado"));
    }
}
