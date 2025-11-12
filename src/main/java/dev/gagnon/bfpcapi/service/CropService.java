package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.Crop;

import java.util.List;

public interface CropService {
    List<Crop> getAllCrops();
    List<Crop> getCropsByCategory(String category);
    Crop getCropById(Long cropId);
    Crop createCrop(Crop crop);
}