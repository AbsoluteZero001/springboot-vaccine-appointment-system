package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Long userId, Long vaccineId, Integer rating, String content);

    List<Review> getReviewsByVaccine(Long vaccineId);

    List<Review> getReviewsByUser(Long userId);

    boolean hasUserReviewed(Long userId, Long vaccineId);
}
