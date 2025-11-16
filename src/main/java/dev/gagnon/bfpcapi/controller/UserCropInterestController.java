package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.UserCropInterest;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.dto.response.UserCropInterestResponse;
import dev.gagnon.bfpcapi.exception.BFPCBaseException;
import dev.gagnon.bfpcapi.service.UserCropInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-crop-interests")
@RequiredArgsConstructor
public class UserCropInterestController {
    
    private final UserCropInterestService userCropInterestService;

    @PostMapping("/add")
    public ResponseEntity<?> createCropInterest(@RequestBody Map<String, Object> request) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Long cropId = Long.valueOf(request.get("cropId").toString());
            UserCropInterest interest = userCropInterestService.createCropInterest(email, cropId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, interest));
        }catch (BFPCBaseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserCropInterests() {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            List<UserCropInterestResponse> interests = userCropInterestService.getUserCropInterests(email);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, interests));
        }catch (BFPCBaseException e){
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, e.getMessage()));
        }

    }

    @GetMapping("/by-id")
    public ResponseEntity<?> getById(@RequestParam Long id) {
        try{
            UserCropInterestResponse interest = userCropInterestService.getUserCropInterest(id);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, interest));
        }
        catch (BFPCBaseException exception){
            return ResponseEntity.ok(new BfpcApiResponse<>(false, exception));
        }

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCropInterests() {
        List<UserCropInterestResponse> interests = userCropInterestService.getAllCropInterests();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, interests));
    }

    @DeleteMapping("/{interestId}")
    public ResponseEntity<?> deleteCropInterest(@PathVariable Long interestId) {
        try{
            userCropInterestService.deleteCropInterest(interestId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, null));
        }catch (BFPCBaseException e){
            return new ResponseEntity<>(new BfpcApiResponse<>(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }
}