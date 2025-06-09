package com.snakegame.backend.controller;

import com.snakegame.backend.model.Score;
import com.snakegame.backend.model.UserHighScore;
import com.snakegame.backend.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
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
        return scoreRepository.findAllByOrderByScoreDesc();
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

    @GetMapping("/highscores")
    public List<UserHighScore> getHighScores() {
        return scoreRepository.findHighScoresGroupByUser();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserHighScore>> getScoresByTimeRange(@RequestParam("type") String type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        switch (type.toLowerCase()) {
            case "week":
                startDate = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(); // đầu tuần
                break;
            case "month":
                startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay(); // đầu tháng
                break;
            default:
                return ResponseEntity.badRequest().build(); // tham số không hợp lệ
        }

        System.out.println("Start: " + startDate);
        System.out.println("Now: " + now);

        List<UserHighScore> scores = scoreRepository.findScoresByDateRange(startDate, now);
        System.out.println("Returning scores: " + scores.size());
        return ResponseEntity.ok(scores);
    }


}
