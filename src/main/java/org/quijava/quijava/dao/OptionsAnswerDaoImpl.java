package org.quijava.quijava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.quijava.quijava.models.OptionsAnswerModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public class OptionsAnswerDaoImpl implements OptionsAnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<OptionsAnswerModel> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(OptionsAnswerModel.class, id));
    }

    @Override
    public OptionsAnswerModel update(OptionsAnswerModel optionAnswer) {
        return entityManager.merge(optionAnswer);
    }

    @Override
    public Set<OptionsAnswerModel> saveAll(Set<OptionsAnswerModel> optionsAnswers) {
        Set<OptionsAnswerModel> savedEntities = new HashSet<>();

        for (OptionsAnswerModel optionsAnswer : optionsAnswers) {
            if (optionsAnswer.getId() == null) {
                entityManager.persist(optionsAnswer);
            } else {
                optionsAnswer = entityManager.merge(optionsAnswer);
            }
            savedEntities.add(optionsAnswer);
        }

        return savedEntities;
    }

    @Override
    public OptionsAnswerModel save(OptionsAnswerModel optionsAnswer) {
        entityManager.persist(optionsAnswer);
        return optionsAnswer;
    }


    @Override
    public void deleteByQuizId(Integer quizId) {
        Query query = entityManager.createQuery("DELETE FROM OptionsAnswerModel oa WHERE oa.question.quiz.id = :quizId");
        query.setParameter("quizId", quizId);
        query.executeUpdate();
    }

    @Override
    public void deleteByQuestionId(Integer questionId) {
        Query query = entityManager.createQuery("DELETE FROM OptionsAnswerModel oa WHERE oa.question.id = :questionId");
        query.setParameter("questionId", questionId);
        query.executeUpdate();
    }
}
