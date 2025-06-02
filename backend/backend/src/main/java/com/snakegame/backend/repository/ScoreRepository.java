package com.snakegame.backend.repository;

import com.snakegame.backend.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findAllByOrderByScoreDesc();
    List<Score> findTop10ByOrderByScoreDesc();            // Top 10 điểm cao nhất
    List<Score> findByUsername(String username);           // Lấy điểm theo người dùng
}
