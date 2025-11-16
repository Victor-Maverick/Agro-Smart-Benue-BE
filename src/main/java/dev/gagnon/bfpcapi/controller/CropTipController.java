package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.dto.request.CropTipRequest;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.dto.response.CropTipResponse;
import dev.gagnon.bfpcapi.service.CropTipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crop-tips")
@RequiredArgsConstructor
public class CropTipController {
    
    private final CropTipService cropTipService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BfpcApiResponse<CropTipResponse>> createCropTip(@ModelAttribute CropTipRequest request) {
        CropTipResponse response = cropTipService.createCropTip(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfpcApiResponse<>(true, response));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<BfpcApiResponse<CropTipResponse>> updateCropTip(
            @PathVariable Long id,
            @ModelAttribute CropTipRequest request) {
        CropTipResponse response = cropTipService.updateCropTip(id, request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BfpcApiResponse<Void>> deleteCropTip(@PathVariable Long id) {
        cropTipService.deleteCropTip(id);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BfpcApiResponse<CropTipResponse>> getCropTipById(@PathVariable Long id) {
        CropTipResponse response = cropTipService.getCropTipById(id);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
    }

    @GetMapping("/all")
    public ResponseEntity<BfpcApiResponse<List<CropTipResponse>>> getAllCropTips() {
        List<CropTipResponse> response = cropTipService.getAllCropTips();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
    }
}
