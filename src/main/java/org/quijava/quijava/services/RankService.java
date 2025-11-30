package org.quijava.quijava.services;

import org.quijava.quijava.dao.RankingDao;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.RankingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    private final LoginService loginService;
    private final SessionPreferencesService sessionPreferencesService;
    private final RankingDao rankingDao;

    @Autowired
    public RankService(LoginService loginService, SessionPreferencesService sessionPreferencesService, RankingDao rankingDao) {
        this.loginService = loginService;
        this.sessionPreferencesService = sessionPreferencesService;
        this.rankingDao = rankingDao;
    }

    public void saveRanking(QuizModel quiz, int totalScore, Duration totalTimeSpent) {
        RankingModel ranking = new RankingModel();
        ranking.setQuiz(quiz);
        ranking.setUser(loginService.findById(sessionPreferencesService.getSessionUserId()));
        ranking.setTotalScore(totalScore);
        ranking.setTotalTime(totalTimeSpent);
        rankingDao.save(ranking);
    }

    public List<RankingModel> getAllRankingsSorted(QuizModel quiz) {
        List<RankingModel> rankings = rankingDao.findAllRankByQuizId(quiz.getId());

        Comparator<RankingModel> comparator = Comparator
                .comparing(RankingModel::getTotalScore).reversed()
                .thenComparing(Comparator.comparing(RankingModel::getTotalTime));

        return rankings.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

}
