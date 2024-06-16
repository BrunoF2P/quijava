package org.quijava.quijava.dao;

import org.quijava.quijava.models.OptionsAnswerModel;

import java.util.List;
import java.util.Optional;

public interface OptionsAnswerDao {
    List<OptionsAnswerModel> saveAll(List<OptionsAnswerModel> optionsAnswers);

    OptionsAnswerModel save(OptionsAnswerModel optionsAnswer);

    void deleteByQuizId(Integer quizId);

    Optional<OptionsAnswerModel> findById(Integer optionAnswerId);

    OptionsAnswerModel update(OptionsAnswerModel existingOptionAnswer);

    void deleteByQuestionId(Integer questionId);
}
