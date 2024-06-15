package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.quijava.quijava.models.QuizModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class QuizDaoImpl implements QuizDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public QuizModel save(QuizModel quiz) {
        entityManager.persist(quiz);
        return quiz;
    }

    @Override
    public void delete(QuizModel quiz) {
        entityManager.remove(quiz);
    }

    @Override
    public QuizModel update(QuizModel quiz) {
        return entityManager.merge(quiz);
    }

    @Override
    @Transactional
    public void deleteQuizById(Integer quizId) {
        QuizModel quiz = entityManager.find(QuizModel.class, quizId);
        if (quiz != null) {
            entityManager.remove(quiz);
        }
    }

    @Override
    public Optional<QuizModel> findById(Integer id) {
        QuizModel quiz = entityManager.find(QuizModel.class, id);
        return Optional.ofNullable(quiz);
    }

    @Override
    public List<QuizModel> findQuizzesWithQuestions() {
        String jpql = "SELECT DISTINCT q FROM QuizModel q JOIN FETCH q.questions JOIN FETCH q.categories";
        TypedQuery<QuizModel> query = entityManager.createQuery(jpql, QuizModel.class);
        return query.getResultList();
    }

    @Override
    public List<QuizModel> findAllQuizzesByUserId(Integer userId) {
        return entityManager.createQuery(
                        "SELECT q FROM QuizModel q WHERE q.author.id = :userId", QuizModel.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<QuizModel> findByCategoriesId(Integer categoryId) {
        return entityManager.createQuery(
                        "SELECT q FROM QuizModel q JOIN q.categories c WHERE c.id = :categoryId", QuizModel.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }
}
