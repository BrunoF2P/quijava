package org.quijava.quijava.services;


import org.quijava.quijava.dao.*;
import org.quijava.quijava.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizDao quizDao;
    private final CategoryDao categoryDao;
    private final SessionPreferencesModel sessionPreferencesModel;
    private final UserDao userDao;
    private final CategoryService categoryService;
    private final OptionsAnswerDao optionsAnswerDao;
    private final QuestionDao questionDao;

    @Autowired
    public QuizService(QuizDao quizDao, UserDao userDao, SessionPreferencesModel sessionPreferencesModel, CategoryDao categoryDao, CategoryService categoryService, OptionsAnswerDao optionsAnswerDao, QuestionDao questionDao) {
        this.quizDao = quizDao;
        this.categoryDao = categoryDao;
        this.sessionPreferencesModel = sessionPreferencesModel;
        this.userDao = userDao;
        this.categoryService = categoryService;
        this.optionsAnswerDao = optionsAnswerDao;
        this.questionDao = questionDao;
    }


    public QuizModel createQuiz(String title, String description, Set<String> selectedCategories, byte[] image) {
        Set<Integer> categoryIds = selectedCategories.stream()
                .map(categoryDao::findByDescription)
                .filter(Objects::nonNull)
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
        return questionDao.findByQuizId(quizId);
    }

    public List<QuizModel> findAllQuizzesByUserId(Integer userId) {
        return quizDao.findAllQuizzesByUserId(userId);
    }

    public void deleteQuiz(Integer quizId) {
        optionsAnswerDao.deleteByQuizId(quizId);
        quizDao.deleteQuizById(quizId);
    }

    public QuizModel updateCategoryQuiz(Integer quizId, Set<String> newCategories) {
        Optional<QuizModel> optionalQuiz = quizDao.findById(quizId);
        if (optionalQuiz.isPresent()) {
            QuizModel quiz = optionalQuiz.get();
            Set<Integer> categoryIds = newCategories.stream()
                    .map(categoryDao::findByDescription)
                    .filter(Objects::nonNull)
                    .map(CategoryModel::getId)
                    .collect(Collectors.toSet());

            Set<CategoryModel> categories = categoryService.findCategoriesByIds(categoryIds);
            quiz.setCategories(categories);
            return quizDao.update(quiz);
        } else {
            throw new NoSuchElementException("Quiz não encontrado");
        }
    }


    public void updateQuiz(Integer id, String title, String description, Set<String> newCategories, byte[] image) {
        QuizModel quiz = quizDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        Set<Integer> categoryIds = newCategories.stream()
                .map(categoryDao::findByDescription)
                .filter(Objects::nonNull)
                .map(CategoryModel::getId)
                .collect(Collectors.toSet());
        Set<CategoryModel> categories = categoryService.findCategoriesByIds(categoryIds);
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setCategories(categories);
        if (image != null) {
            quiz.setImageQuiz(image);
        }

        quizDao.update(quiz);
    }

    public List<QuizModel> findQuizzesByCategory(Integer categoryId) {
        return quizDao.findByCategoriesId(categoryId);
    }

    public void countPlayQuiz(QuizModel quiz){
        quiz.setTotalAttempts(quiz.getTotalAttempts() + 1);
        quizDao.update(quiz);
    }
    private UserModel getLoggedInUser() {

        int userId = sessionPreferencesModel.getUserId();

        return userDao.findById(userId);
    }
}
