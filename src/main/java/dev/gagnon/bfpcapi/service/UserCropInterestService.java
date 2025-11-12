package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.UserCropInterest;

import java.util.List;

public interface UserCropInterestService {
    UserCropInterest createCropInterest(String userEmail, Long cropId, boolean priceAlerts, boolean marketUpdates);
    List<UserCropInterest> getUserCropInterests(String userEmail);
    void deleteCropInterest(Long interestId);
}