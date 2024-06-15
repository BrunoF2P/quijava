package org.quijava.quijava.services;

import org.quijava.quijava.dao.OptionsAnswerDao;
import org.quijava.quijava.dao.QuestionDao;
import org.quijava.quijava.models.OptionsAnswerModel;
import org.quijava.quijava.models.QuestionModel;
import org.quijava.quijava.models.QuestionDifficulty;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.TypeQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@Service
public class QuestionService {

    private final QuestionDao questionDao;
    private final OptionsAnswerDao optionsAnswerDao;

    @Autowired
    public QuestionService(QuestionDao questionDao, OptionsAnswerDao optionsAnswerDao) {
        this.questionDao = questionDao;
        this.optionsAnswerDao = optionsAnswerDao;
    }

    @Transactional
    public QuestionModel createQuestion(String questionText, QuizModel quiz, TypeQuestion typeQuestion, String durationText, Set<OptionsAnswerModel> optionsAnswers, QuestionDifficulty difficulty, byte[] image) {
        QuestionModel newQuestion = new QuestionModel();

        newQuestion.setQuestionText(questionText);
        newQuestion.setQuiz(quiz);
        newQuestion.setTypeQuestion(typeQuestion);
        newQuestion.setQuestionDifficulty(difficulty);
        newQuestion.setImageQuestion(image);

        try {
            long durationSeconds = Long.parseLong(durationText);
            Duration duration = Duration.ofSeconds(durationSeconds);
            newQuestion.setLimiteTime(duration);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter a duração para segundos.");
        }

        // Associar as respostas à pergunta
        Set<OptionsAnswerModel> associatedAnswers = createOptionsAnswers(newQuestion, optionsAnswers);
        newQuestion.setOptionsAnswers(associatedAnswers);

        return questionDao.save(newQuestion);
    }


    @Transactional
    public Set<OptionsAnswerModel> createOptionsAnswers(QuestionModel question, Set<OptionsAnswerModel> newAnswers) {
        Set<OptionsAnswerModel> optionsAnswers = new HashSet<>();

        if (newAnswers != null) {
            for (OptionsAnswerModel newAnswer : newAnswers) {
                // Assegure-se de que cada nova resposta esteja associada à pergunta correta
                newAnswer.setQuestion(question);
                optionsAnswers.add(newAnswer);
            }
        }

        return optionsAnswers;
    }



    @Transactional
    public QuestionModel updateQuestion(QuestionModel question) {
        Optional<QuestionModel> optionalQuestion = questionDao.findById(question.getId());
        if (optionalQuestion.isPresent()) {
            QuestionModel existingQuestion = optionalQuestion.get();

            existingQuestion.setQuestionText(question.getQuestionText());
            existingQuestion.setQuiz(question.getQuiz());
            existingQuestion.setTypeQuestion(question.getTypeQuestion());
            existingQuestion.setQuestionDifficulty(question.getQuestionDifficulty());
            existingQuestion.setImageQuestion(question.getImageQuestion());
            existingQuestion.setLimiteTime(question.getLimiteTime());

            // Limpa as opções de resposta atuais da pergunta no banco de dados
            optionsAnswerDao.deleteByQuestionId(existingQuestion.getId());

            // Limpa as opções de resposta atuais da pergunta em memória
            existingQuestion.getOptionsAnswers().clear();

            // Atualizar as opções de resposta associadas à pergunta
            Set<OptionsAnswerModel> updatedOptionsAnswers = createOptionsAnswers(existingQuestion, question.getOptionsAnswers());
            existingQuestion.getOptionsAnswers().addAll(updatedOptionsAnswers);

            return questionDao.update(existingQuestion);
        } else {
            throw new IllegalArgumentException("Pergunta não encontrada.");
        }
    }


    @Transactional(readOnly = true)
    public List<QuestionModel> getAllQuestions() {
        return questionDao.findAll();
    }


    @Transactional
    public QuestionModel saveQuestion(QuestionModel question) {
        return questionDao.save(question);
    }

    @Transactional
    public QuestionModel update(QuestionModel question) {
        return questionDao.update(question);
    }

    @Transactional
    public void deleteQuestion(Integer question) {
        optionsAnswerDao.deleteByQuestionId(question);
        questionDao.delete(question);
    }

    public Optional<QuestionModel> findById(Integer id) {
        return questionDao.findById(id);
    }

    @Transactional
    public void saveOptionsAnswers(Set<OptionsAnswerModel> optionsAnswers) {
        optionsAnswerDao.saveAll(optionsAnswers);
    }
}
