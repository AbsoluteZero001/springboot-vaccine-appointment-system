package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.Review;
import com.springboot.vaccineappointmentsystem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long vaccineId = Long.valueOf(payload.get("vaccineId").toString());
            Integer rating = Integer.valueOf(payload.get("rating").toString());
            String content = payload.get("content") != null ? payload.get("content").toString() : null;
            Review review = reviewService.createReview(userId, vaccineId, rating, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/vaccine/{vaccineId}")
    public ResponseEntity<List<Review>> getReviewsByVaccine(@PathVariable Long vaccineId) {
        return ResponseEntity.ok(reviewService.getReviewsByVaccine(vaccineId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> hasReviewed(
            @RequestParam Long userId, @RequestParam Long vaccineId) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("reviewed", reviewService.hasUserReviewed(userId, vaccineId));
        return ResponseEntity.ok(result);
    }
}
