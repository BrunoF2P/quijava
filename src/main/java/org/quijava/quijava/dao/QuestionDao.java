package org.quijava.quijava.dao;

import org.quijava.quijava.models.QuestionModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionDao {

    Set<QuestionModel> saveAll(Set<QuestionModel> questions);

    QuestionModel save(QuestionModel question);

    void deleteByQuizId(Integer quizId);

    List<QuestionModel> findAll();

    List<QuestionModel> findByQuizId(Integer quizId);

    QuestionModel update(QuestionModel question);

    void delete(Integer question);

    Optional<QuestionModel> findById(Integer questionId);
}
