package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.dto.request.CropTipRequest;
import dev.gagnon.bfpcapi.dto.response.CropTipResponse;

import java.util.List;

public interface CropTipService {
    CropTipResponse createCropTip(CropTipRequest request);
    CropTipResponse updateCropTip(Long id, CropTipRequest request);
    void deleteCropTip(Long id);
    CropTipResponse getCropTipById(Long id);
    List<CropTipResponse> getAllCropTips();
}
