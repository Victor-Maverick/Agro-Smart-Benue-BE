package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.data.model.Crop;
import dev.gagnon.bfpcapi.data.repository.CropRepository;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {
    
    private final CropRepository cropRepository;

    @Override
    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    @Override
    public List<Crop> getCropsByCategory(String category) {
        return cropRepository.findByCategory(category);
    }

    @Override
    public Crop getCropById(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new BusinessException("Crop not found"));
    }

    @Override
    public Crop createCrop(Crop crop) {
        return cropRepository.save(crop);
    }
}