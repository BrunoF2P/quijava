package org.quijava.quijava.dao;


import jakarta.persistence.Query;
import org.quijava.quijava.models.OptionsAnswerModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
@Transactional
public class OptionsAnswerDaoImpl implements OptionsAnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Set<OptionsAnswerModel> saveAll(Set<OptionsAnswerModel> optionsAnswers) {
        for (OptionsAnswerModel optionsAnswer : optionsAnswers) {
            entityManager.persist(optionsAnswer);
        }
        return optionsAnswers;
    }

    @Override
    public void deleteByQuizId(Integer quizId) {
        Query query = entityManager.createQuery("DELETE FROM OptionsAnswerModel oa WHERE oa.question.quiz.id = :quizId");
        query.setParameter("quizId", quizId);
        query.executeUpdate();
    }
}
