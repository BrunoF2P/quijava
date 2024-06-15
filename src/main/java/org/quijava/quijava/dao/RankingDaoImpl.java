package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.quijava.quijava.models.RankingModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class RankingDaoImpl implements RankingDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RankingModel save(RankingModel rank) {
        entityManager.persist(rank);
        return rank;
    }

    @Override
    public List<RankingModel> findAll() {
        return entityManager.createQuery("SELECT c FROM RankingModel c", RankingModel.class)
                .getResultList();
    }

    @Override
    public List<RankingModel> findAllRankByQuizId(Integer quizId) {
        return entityManager.createQuery(
                        "SELECT r FROM RankingModel r JOIN FETCH r.user WHERE r.quiz.id = :quizId", RankingModel.class)
                .setParameter("quizId", quizId)
                .getResultList();
    }
}
