package org.quijava.quijava.dao;

import org.jetbrains.annotations.NotNull;
import org.quijava.quijava.models.RankingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingDao extends JpaRepository<@NotNull RankingModel, @NotNull Integer> {

    @Query("SELECT r FROM RankingModel r JOIN FETCH r.user WHERE r.quiz.id = :quizId ORDER BY r.totalScore DESC, r.totalTime ASC")
    List<RankingModel> findAllRankByQuizId(@Param("quizId") Integer quizId);

    @Query("SELECT r FROM RankingModel r JOIN FETCH r.quiz WHERE r.user.id = :userId ORDER BY r.dateCompleted DESC")
    List<RankingModel> findByUserId(@Param("userId") Integer userId);
}
