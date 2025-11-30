package org.quijava.quijava.services;

import org.quijava.quijava.dao.QuestionDao;
import org.quijava.quijava.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {

    private final QuestionDao questionDao;

    public QuestionService(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    public QuestionModel createQuestion(String questionText,
                                        QuizModel quiz,
                                        TypeQuestion typeQuestion,
                                        String durationText,
                                        List<OptionsAnswerModel> optionsAnswers,
                                        QuestionDifficulty difficulty,
                                        byte[] image) {
        QuestionModel question = new QuestionModel();
        question.setQuestionText(questionText);
        question.setQuiz(quiz);
        question.setTypeQuestion(typeQuestion);
        question.setQuestionDifficulty(difficulty);
        question.setImageQuestion(image);
        question.setLimiteTime(parseDuration(durationText));

        associateOptionsToQuestion(question, optionsAnswers);

        return questionDao.save(question);
    }

    public QuestionModel updateQuestion(QuestionModel question) {
        QuestionModel existingQuestion = questionDao.findById(question.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada."));

        existingQuestion.setQuestionText(question.getQuestionText());
        existingQuestion.setQuiz(question.getQuiz());
        existingQuestion.setTypeQuestion(question.getTypeQuestion());
        existingQuestion.setQuestionDifficulty(question.getQuestionDifficulty());
        existingQuestion.setImageQuestion(question.getImageQuestion());
        existingQuestion.setLimiteTime(question.getLimiteTime());

        existingQuestion.getOptionsAnswers().clear();
        associateOptionsToQuestion(existingQuestion, question.getOptionsAnswers());

        return questionDao.save(existingQuestion);
    }

    public void deleteQuestion(Integer questionId) {
        questionDao.deleteById(questionId);
    }

    public Optional<QuestionModel> findById(Integer id) {
        return questionDao.findById(id);
    }

    public List<QuestionModel> findQuestionsByQuiz(Integer quizId) {
        return questionDao.findByQuizId(quizId);
    }

    private Duration parseDuration(String durationText) {
        try {
            long seconds = Long.parseLong(durationText);
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Duração inválida: " + durationText, e);
        }
    }

    private void associateOptionsToQuestion(QuestionModel question, List<OptionsAnswerModel> options) {
        if (options != null) {
            options.forEach(option -> {
                option.setQuestion(question);
                question.getOptionsAnswers().add(option);
            });
        }
    }
}
