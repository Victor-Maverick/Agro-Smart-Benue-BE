package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.Crop;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
public class CropController {
    
    private final CropService cropService;

    @GetMapping
    public ResponseEntity<BfpcApiResponse<List<Crop>>> getAllCrops() {
        List<Crop> crops = cropService.getAllCrops();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, crops));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<BfpcApiResponse<List<Crop>>> getCropsByCategory(@PathVariable String category) {
        List<Crop> crops = cropService.getCropsByCategory(category);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, crops));
    }

    @GetMapping("/{cropId}")
    public ResponseEntity<BfpcApiResponse<Crop>> getCrop(@PathVariable Long cropId) {
        Crop crop = cropService.getCropById(cropId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, crop));
    }

}