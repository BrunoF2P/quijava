package org.quijava.quijava.dao;

import org.quijava.quijava.models.QuestionModel;

import java.util.List;
import java.util.Set;

public interface QuestionDao {

    Set<QuestionModel> saveAll(Set<QuestionModel> questions);

    void deleteByQuizId(Integer quizId);

    List<QuestionModel> findByQuizId(Integer quizId);
}
