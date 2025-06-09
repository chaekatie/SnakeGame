package com.snakegame.backend.controller;

import com.snakegame.backend.dto.MatchRequest;
import com.snakegame.backend.model.Match;
import com.snakegame.backend.model.User;
import com.snakegame.backend.repository.UserRepository;
import com.snakegame.backend.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody MatchRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = new Match();
        match.setUser(user);
        match.setTotalScore(request.getTotalScore());
        match.setPlayTime(request.getPlayTime());
        match.setFood1Count(request.getFood1Count());
        match.setFood2Count(request.getFood2Count());
        match.setFood3Count(request.getFood3Count());

        return ResponseEntity.ok(matchService.saveMatch(match));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        return ResponseEntity.ok(matchService.getUserStats(user));
    }
}
