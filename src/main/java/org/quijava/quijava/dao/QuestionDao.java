package org.quijava.quijava.dao;

import org.jetbrains.annotations.NotNull;
import org.quijava.quijava.models.QuestionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<@NotNull QuestionModel, @NotNull Integer> {

    List<QuestionModel> findByQuizId(Integer quizId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionModel q WHERE q.quiz.id = :quizId")
    void deleteByQuizId(@Param("quizId") Integer quizId);

    @Query("SELECT q FROM QuestionModel q LEFT JOIN FETCH q.optionsAnswers WHERE q.quiz.id = :quizId")
    List<QuestionModel> findByQuizIdWithOptions(@Param("quizId") Integer quizId);
}
