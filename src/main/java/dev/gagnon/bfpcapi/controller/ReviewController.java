package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.dto.request.ReviewRequest;
import dev.gagnon.bfpcapi.dto.response.ReviewResponse;
import dev.gagnon.bfpcapi.exception.BFPCBaseException;
import dev.gagnon.bfpcapi.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@RequestParam String email, @RequestBody ReviewRequest request) {
        try{
            String response = reviewService.createReview(email,request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<?> getReviewById(@RequestParam Long id) {
        try{
            ReviewResponse response = reviewService.findById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews() {
        try{
            List<ReviewResponse> response = reviewService.findAll();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-by-id")
    public ResponseEntity<?> deleteReviewById(@RequestParam Long id) {
        try{
            String response = reviewService.deleteReview(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateReview(@RequestParam Long id, @RequestBody ReviewRequest request) {
        try{
            String response = reviewService.updateReview(id,request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAll() {
        try{
            String response = reviewService.deleteReviews();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
