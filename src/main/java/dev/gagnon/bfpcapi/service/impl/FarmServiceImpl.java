package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.data.model.Farm;
import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.repository.FarmRepository;
import dev.gagnon.bfpcapi.data.repository.UserRepository;
import dev.gagnon.bfpcapi.dto.request.FarmRequest;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.exception.UserNotFoundException;
import dev.gagnon.bfpcapi.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {
    
    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    @Override
    public Farm createFarm(Long userId, FarmRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Farm farm = Farm.builder()
                .user(user)
                .farmName(request.getFarmName())
                .location(request.getLocation())
                .coordinates(request.getCoordinates())
                .size(request.getSize())
                .soilType(request.getSoilType())
                .description(request.getDescription())
                .build();

        return farmRepository.save(farm);
    }

    @Override
    public List<Farm> getFarmsByUserId(Long userId) {
        return farmRepository.findByUserId(userId);
    }

    @Override
    public Farm getFarmById(Long farmId) {
        return farmRepository.findById(farmId)
                .orElseThrow(() -> new BusinessException("Farm not found"));
    }

    @Override
    public Farm updateFarm(Long farmId, FarmRequest request) {
        Farm farm = getFarmById(farmId);
        
        farm.setFarmName(request.getFarmName());
        farm.setLocation(request.getLocation());
        farm.setCoordinates(request.getCoordinates());
        farm.setSize(request.getSize());
        farm.setSoilType(request.getSoilType());
        farm.setDescription(request.getDescription());

        return farmRepository.save(farm);
    }

    @Override
    public void deleteFarm(Long farmId) {
        Farm farm = getFarmById(farmId);
        farmRepository.delete(farm);
    }
}