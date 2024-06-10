package org.quijava.quijava.dao;

import org.quijava.quijava.models.OptionsAnswerModel;

import java.util.Set;

public interface OptionsAnswerDao {
    Set<OptionsAnswerModel> saveAll(Set<OptionsAnswerModel> optionsAnswers);
    void deleteByQuizId(Integer quizId);
}
