package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.quijava.quijava.models.QuestionModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class QuestionDaoImpl implements QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Set<QuestionModel> saveAll(Set<QuestionModel> questions) {
        for (QuestionModel question : questions) {
            entityManager.persist(question);
        }
        return questions;
    }

    @Override
    public void deleteByQuizId(Integer quizId) {
        Query query = entityManager.createQuery("DELETE FROM QuestionModel q WHERE q.quiz.id = :quizId");
        query.setParameter("quizId", quizId);
        query.executeUpdate();
    }


    @Override
    public List<QuestionModel> findByQuizId(Integer quizId) {
        TypedQuery<QuestionModel> query = entityManager.createQuery(
                "SELECT q FROM QuestionModel q WHERE q.quiz.id = :quizId", QuestionModel.class);
        query.setParameter("quizId", quizId);
        return query.getResultList();
    }
}
