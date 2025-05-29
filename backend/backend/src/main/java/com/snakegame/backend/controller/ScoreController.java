package com.snakegame.backend.controller;

import com.snakegame.backend.model.Score;
import com.snakegame.backend.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private ScoreRepository scoreRepository;

    // Lưu điểm cho tài khoản hiện tại
    @PostMapping
    public ResponseEntity<String> addScore(@RequestBody Score score) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        score.setUsername(username);
        score.setTime(LocalDateTime.now());
        scoreRepository.save(score);

        return ResponseEntity.ok("Score saved");
    }

    // Lấy tất cả điểm
    @GetMapping("/all")
    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    // Lấy điểm người dùng hiện tại
    @GetMapping("/my")
    public List<Score> getMyScores() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return scoreRepository.findByUsername(username);
    }

    // Lấy top 10 bảng xếp hạng
    @GetMapping("/top10")
    public List<Score> getTop10Scores() {
        return scoreRepository.findTop10ByOrderByScoreDesc();
    }
}
