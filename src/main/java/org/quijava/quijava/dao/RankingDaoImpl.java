package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.quijava.quijava.models.RankingModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RankingDaoImpl implements RankingDao{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RankingModel save(RankingModel rank) {
        entityManager.persist(rank);
        return rank;
    }

}
