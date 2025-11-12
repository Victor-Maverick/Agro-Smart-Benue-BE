package dev.gagnon.bfpcapi.dto.response;

import dev.gagnon.bfpcapi.data.model.Review;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private String comment;
    private Double rating;
    private String reviewerName;
    private String reviewerRole;
    private String reviewerLocation;
    private LocalDate reviewedAt;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.reviewedAt = review.getCreatedAt().toLocalDate();
        this.reviewerName = review.getUser().getFirstName() + " " + review.getUser().getLastName();
        //space for reviewer location
        //space for reviewer role
    }
}
