package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.Review;
import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import com.springboot.vaccineappointmentsystem.repository.ReviewRepository;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import com.springboot.vaccineappointmentsystem.repository.VaccineRepository;
import com.springboot.vaccineappointmentsystem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private VaccineRepository vaccineRepository;

    @Override
    public Review createReview(Long userId, Long vaccineId, Integer rating, String content) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Vaccine vaccine = vaccineRepository.findById(vaccineId)
                .orElseThrow(() -> new RuntimeException("疫苗不存在"));
        if (rating == null || rating < 1 || rating > 5)
            throw new RuntimeException("评分必须在1-5之间");
        if (reviewRepository.existsByUserIdAndVaccineId(userId, vaccineId))
            throw new RuntimeException("您已评价过该疫苗");

        Review review = new Review();
        review.setUser(user);
        review.setVaccine(vaccine);
        review.setRating(rating);
        review.setContent(content);
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByVaccine(Long vaccineId) {
        return reviewRepository.findByVaccineIdWithUser(vaccineId);
    }

    @Override
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public boolean hasUserReviewed(Long userId, Long vaccineId) {
        return reviewRepository.existsByUserIdAndVaccineId(userId, vaccineId);
    }
}
