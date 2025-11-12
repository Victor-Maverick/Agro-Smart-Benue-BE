package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.UserCropInterest;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.service.UserCropInterestService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<BfpcApiResponse<UserCropInterest>> createCropInterest(@RequestBody Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Long cropId = Long.valueOf(request.get("cropId").toString());
        boolean priceAlerts = (Boolean) request.getOrDefault("priceAlerts", true);
        boolean marketUpdates = (Boolean) request.getOrDefault("marketUpdates", true);
        
        UserCropInterest interest = userCropInterestService.createCropInterest(email, cropId, priceAlerts, marketUpdates);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, interest));
    }

    @GetMapping
    public ResponseEntity<BfpcApiResponse<List<UserCropInterest>>> getUserCropInterests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        List<UserCropInterest> interests = userCropInterestService.getUserCropInterests(email);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, interests));
    }

    @DeleteMapping("/{interestId}")
    public ResponseEntity<BfpcApiResponse<Void>> deleteCropInterest(@PathVariable Long interestId) {
        userCropInterestService.deleteCropInterest(interestId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, null));
    }
}