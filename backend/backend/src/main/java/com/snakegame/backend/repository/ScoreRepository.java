package com.snakegame.backend.repository;

import com.snakegame.backend.model.Score;
import com.snakegame.backend.model.UserHighScore;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findAllByOrderByScoreDesc();
    List<Score> findTop10ByOrderByScoreDesc();            // Top 10 điểm cao nhất
    List<Score> findByUsername(String username);           // Lấy điểm theo người dùng

    @Query("SELECT s.username AS username, MAX(s.score) AS highscore FROM Score s GROUP BY s.username ORDER BY highscore DESC")
    List<UserHighScore> findHighScoresGroupByUser();

    // Filter by week/month for all users
    @Query("SELECT s.username AS username, MAX(s.score) AS highscore FROM Score s WHERE s.time >= :startDate AND s.time < :endDate GROUP BY s.username ORDER BY highscore DESC")
    List<UserHighScore> findScoresByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Filter by week/month for a specific user
    @Query("SELECT s FROM Score s WHERE s.username = :username AND s.time BETWEEN :start AND :end ORDER BY s.score DESC")
    List<Score> findByUsernameFilter(
        @Param("username") String username,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

}
