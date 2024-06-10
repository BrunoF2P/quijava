package org.quijava.quijava.dao;

import org.quijava.quijava.models.QuizModel;

import java.util.List;
import java.util.Optional;

public interface QuizDao {
    QuizModel save(QuizModel quiz);
    void delete(QuizModel quiz);
    void deleteQuizById(Integer quizId);
    QuizModel update(QuizModel quiz);
    Optional<QuizModel> findById(Integer id);
    List<QuizModel> findQuizzesWithQuestions();
    List<QuizModel> findAllQuizzesByUserId(Integer userId);
}
