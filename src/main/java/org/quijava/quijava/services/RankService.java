package org.quijava.quijava.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import org.quijava.quijava.dao.RankingDao;
import org.quijava.quijava.models.QuizModel;
import org.quijava.quijava.models.RankingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        java.time.Duration totalDuration = java.time.Duration.ofMillis((long) totalTimeSpent.toMillis());
        ranking.setTotalTime(totalDuration);
        rankingDao.save(ranking);
    }

    public ObservableList<RankingModel> getAllRankingsSorted(QuizModel quiz) {
        List<RankingModel> rankings = rankingDao.findAllRankByQuizId(quiz.getId());

        Comparator<RankingModel> comparator = Comparator
                .comparing(RankingModel::getTotalScore).reversed()
                .thenComparing(Comparator.comparing(RankingModel::getTotalTime));

        List<RankingModel> sortedRankings = rankings.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        return FXCollections.observableArrayList(sortedRankings);
    }

}
