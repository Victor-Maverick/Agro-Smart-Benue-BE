package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.data.model.Review;
import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.repository.ReviewRepository;
import dev.gagnon.bfpcapi.data.repository.UserRepository;
import dev.gagnon.bfpcapi.dto.request.ReviewRequest;
import dev.gagnon.bfpcapi.dto.response.ReviewResponse;
import dev.gagnon.bfpcapi.exception.ResourceNotFoundException;
import dev.gagnon.bfpcapi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public String createReview(String email,ReviewRequest request) {
        Review review = modelMapper.map(request, Review.class);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("user not found"));
        review.setUser(user);
        reviewRepository.save(review);
        return "Review added successfully";
    }

    @Override
    public ReviewResponse findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("review not found"));
        return new ReviewResponse(review);
    }

    @Override
    public List<ReviewResponse> findAll() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty())return List.of();
        return reviews.stream().map(ReviewResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("review not found"));
        reviewRepository.delete(review);
        return "review deleted successfully";
    }

    @Override
    public String updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("review not found"));
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        reviewRepository.save(review);
        return "review updated successfully";
    }

    @Override
    public String deleteReviews() {
        List<Review> reviews = reviewRepository.findAll();
        reviewRepository.deleteAll(reviews);
        return "All reviews deleted successfully";
    }
}
