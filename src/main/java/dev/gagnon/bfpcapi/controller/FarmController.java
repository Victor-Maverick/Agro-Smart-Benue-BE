package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.Farm;
import dev.gagnon.bfpcapi.dto.request.FarmRequest;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.service.FarmService;
import dev.gagnon.bfpcapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
public class FarmController {
    
    private final FarmService farmService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BfpcApiResponse<Farm>> createFarm(@RequestBody FarmRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Long userId = userService.getUserIdByEmail(email);
        
        Farm farm = farmService.createFarm(userId, request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, farm));
    }

    @GetMapping
    public ResponseEntity<BfpcApiResponse<List<Farm>>> getUserFarms() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Long userId = userService.getUserIdByEmail(email);
        
        List<Farm> farms = farmService.getFarmsByUserId(userId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, farms));
    }

    @GetMapping("/{farmId}")
    public ResponseEntity<BfpcApiResponse<Farm>> getFarm(@PathVariable Long farmId) {
        Farm farm = farmService.getFarmById(farmId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, farm));
    }

    @PutMapping("/{farmId}")
    public ResponseEntity<BfpcApiResponse<Farm>> updateFarm(@PathVariable Long farmId, @RequestBody FarmRequest request) {
        Farm farm = farmService.updateFarm(farmId, request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, farm));
    }

    @DeleteMapping("/{farmId}")
    public ResponseEntity<BfpcApiResponse<Void>> deleteFarm(@PathVariable Long farmId) {
        farmService.deleteFarm(farmId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, null));
    }
}