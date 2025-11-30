package org.quijava.quijava.dao;

import org.jetbrains.annotations.NotNull;
import org.quijava.quijava.models.QuizModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizDao extends JpaRepository<@NotNull QuizModel, @NotNull Integer> {

    @Query("SELECT DISTINCT q FROM QuizModel q " +
            "LEFT JOIN FETCH q.questions " +
            "LEFT JOIN FETCH q.categories")
    List<QuizModel> findQuizzesWithQuestions();

    List<QuizModel> findByAuthorId(Integer userId);

    @Query("SELECT q FROM QuizModel q JOIN q.categories c WHERE c.id = :categoryId")
    List<QuizModel> findByCategoriesId(@Param("categoryId") Integer categoryId);
}
