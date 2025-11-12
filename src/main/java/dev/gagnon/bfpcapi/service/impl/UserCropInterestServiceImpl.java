package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.data.model.Crop;
import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.model.UserCropInterest;
import dev.gagnon.bfpcapi.data.repository.UserCropInterestRepository;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.service.CropService;
import dev.gagnon.bfpcapi.service.UserCropInterestService;
import dev.gagnon.bfpcapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCropInterestServiceImpl implements UserCropInterestService {
    
    private final UserCropInterestRepository userCropInterestRepository;
    private final UserService userService;
    private final CropService cropService;

    @Override
    public UserCropInterest createCropInterest(String userEmail, Long cropId, boolean priceAlerts, boolean marketUpdates) {
        User user = userService.getUserByEmail(userEmail);
        Crop crop = cropService.getCropById(cropId);

        UserCropInterest interest = UserCropInterest.builder()
                .user(user)
                .crop(crop)
                .priceAlerts(priceAlerts)
                .marketUpdates(marketUpdates)
                .build();

        return userCropInterestRepository.save(interest);
    }

    @Override
    public List<UserCropInterest> getUserCropInterests(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        return userCropInterestRepository.findByUser(user);
    }

    @Override
    public void deleteCropInterest(Long interestId) {
        UserCropInterest interest = userCropInterestRepository.findById(interestId)
                .orElseThrow(() -> new BusinessException("Crop interest not found"));
        userCropInterestRepository.delete(interest);
    }
}