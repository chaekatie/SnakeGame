package com.snakegame.backend.service;

import com.snakegame.backend.dto.UserStatsResponse;
import com.snakegame.backend.model.Match;
import com.snakegame.backend.model.User;
import com.snakegame.backend.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public Match saveMatch(Match match) {
        return matchRepository.save(match);
    }
    public UserStatsResponse getUserStats(User user) {
        List<Match> matches = matchRepository.findByUser(user);

        int totalMatches = matches.size();
        int totalFood1 = matches.stream().mapToInt(Match::getFood1Count).sum();
        int totalFood2 = matches.stream().mapToInt(Match::getFood2Count).sum();
        int totalFood3 = matches.stream().mapToInt(Match::getFood3Count).sum();
        int totalPlayTime = matches.stream().mapToInt(Match::getPlayTime).sum();
        int highestScore = matchRepository.findHighestScoreByUser(user) != null
            ? matchRepository.findHighestScoreByUser(user) : 0;

        return new UserStatsResponse(totalMatches, totalFood1, totalFood2, totalFood3, totalPlayTime, highestScore);
    }
}
