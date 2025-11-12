package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.Farm;
import dev.gagnon.bfpcapi.dto.request.FarmRequest;

import java.util.List;

public interface FarmService {
    Farm createFarm(Long userId, FarmRequest request);
    List<Farm> getFarmsByUserId(Long userId);
    Farm getFarmById(Long farmId);
    Farm updateFarm(Long farmId, FarmRequest request);
    void deleteFarm(Long farmId);
}