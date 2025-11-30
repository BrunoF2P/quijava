package org.quijava.quijava.dao;

import org.jetbrains.annotations.NotNull;
import org.quijava.quijava.models.OptionsAnswerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OptionsAnswerDao extends JpaRepository<@NotNull OptionsAnswerModel, @NotNull Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM OptionsAnswerModel oa WHERE oa.question.quiz.id = :quizId")
    void deleteByQuizId(@Param("quizId") Integer quizId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OptionsAnswerModel oa WHERE oa.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") Integer questionId);
}
