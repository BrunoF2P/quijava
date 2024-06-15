package org.quijava.quijava.dao;

import org.quijava.quijava.models.RankingModel;

import java.util.List;

public interface RankingDao {
    RankingModel save(RankingModel rank);

    List<RankingModel> findAll();

    List<RankingModel> findAllRankByQuizId(Integer quizId);
}
