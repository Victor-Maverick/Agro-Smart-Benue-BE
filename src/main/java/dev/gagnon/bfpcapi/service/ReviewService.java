package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.dto.request.ReviewRequest;
import dev.gagnon.bfpcapi.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    String createReview(String email,ReviewRequest request);

    ReviewResponse findById(Long id);

    List<ReviewResponse> findAll();

    String deleteReview(Long id);

    String updateReview(Long id, ReviewRequest request);

    String deleteReviews();
}
