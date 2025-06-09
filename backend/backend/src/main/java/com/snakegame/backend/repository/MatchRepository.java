package com.snakegame.backend.repository;

import com.snakegame.backend.model.Match;
import com.snakegame.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUser(User user);

    @Query("SELECT MAX(m.totalScore) FROM Match m WHERE m.user = :user")
    Integer findHighestScoreByUser(User user);
}
