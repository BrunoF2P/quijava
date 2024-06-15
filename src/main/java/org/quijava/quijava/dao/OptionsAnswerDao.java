package org.quijava.quijava.dao;

import org.quijava.quijava.models.OptionsAnswerModel;

import java.util.Optional;
import java.util.Set;

public interface OptionsAnswerDao {
    Set<OptionsAnswerModel> saveAll(Set<OptionsAnswerModel> optionsAnswers);

    OptionsAnswerModel save(OptionsAnswerModel optionsAnswer);

    void deleteByQuizId(Integer quizId);

    Optional<OptionsAnswerModel> findById(Integer optionAnswerId);

    OptionsAnswerModel update(OptionsAnswerModel existingOptionAnswer);

    void deleteByQuestionId(Integer questionId);
}
