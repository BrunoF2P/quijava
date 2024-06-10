package org.quijava.quijava.services;

import org.quijava.quijava.dao.OptionsAnswerDao;
import org.quijava.quijava.dao.QuestionDao;
import org.quijava.quijava.models.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Service
public class QuestionService {

    private final QuestionDao questionDao;
    private final OptionsAnswerDao optionsAnswerDao;

    @Autowired
    public QuestionService(QuestionDao questionDao, OptionsAnswerDao optionsAnswerDao) {
        this.questionDao = questionDao;
        this.optionsAnswerDao = optionsAnswerDao;
    }

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
        Set<OptionsAnswerModel> associatedAnswers = createOptionsAnswers( newQuestion, optionsAnswers);
        newQuestion.setOptionsAnswers(associatedAnswers);

        return newQuestion;
    }

    public OptionsAnswerModel createOptionAnswer(QuestionModel question, String text, boolean isCorrect, String scoreText) {
        OptionsAnswerModel optionAnswer = new OptionsAnswerModel();
        optionAnswer.setOptionText(text);
        optionAnswer.setIsCorrect(isCorrect);

        try {
            int score = Integer.parseInt(scoreText);
            optionAnswer.setScore(isCorrect ? score : 0);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter a pontuação para um número inteiro.");
        }
        optionAnswer.setQuestion(question);
        return optionAnswer;
    }

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

    public void saveQuestions(Set<QuestionModel> questions) {
        questionDao.saveAll(questions);
    }

    public void saveOptionsAnswers(Set<OptionsAnswerModel> optionsAnswers) {
        optionsAnswerDao.saveAll(optionsAnswers);
    }
}
