package org.quijava.quijava.services;

import org.quijava.quijava.dao.RankingDao;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.RankingModel;
import org.quijava.quijava.models.UserModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
public class RankService {

    private final LoginService loginService;
    private final SessionPreferencesService sessionPreferences;
    private final RankingDao rankingDao;

    public RankService(LoginService loginService,
                       SessionPreferencesService sessionPreferences,
                       RankingDao rankingDao) {
        this.loginService = loginService;
        this.sessionPreferences = sessionPreferences;
        this.rankingDao = rankingDao;
    }

    public void saveRanking(QuizModel quiz, int totalScore, Duration totalTimeSpent) {
        UserModel user = sessionPreferences.getUserId()
                .flatMap(loginService::findById)
                .orElseThrow(() -> new IllegalStateException("Usuário não está logado"));

        RankingModel ranking = new RankingModel();
        ranking.setQuiz(quiz);
        ranking.setUser(user);
        ranking.setTotalScore(totalScore);
        ranking.setTotalTime(totalTimeSpent);

        rankingDao.save(ranking);
    }

    public List<RankingModel> getAllRankingsSorted(QuizModel quiz) {
        return rankingDao.findAllRankByQuizId(quiz.getId());
    }

    public List<RankingModel> getUserHistory(UserModel user) {
        return rankingDao.findByUserId(user.getId());
    }
}
