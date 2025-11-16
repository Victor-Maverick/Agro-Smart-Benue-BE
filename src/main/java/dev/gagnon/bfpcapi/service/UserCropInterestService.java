package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.UserCropInterest;
import dev.gagnon.bfpcapi.dto.response.UserCropInterestResponse;

import java.util.List;

public interface UserCropInterestService {
    UserCropInterest createCropInterest(String userEmail, Long cropId);
    List<UserCropInterestResponse> getUserCropInterests(String userEmail);
    void deleteCropInterest(Long interestId);

    List<UserCropInterestResponse> getAllCropInterests();

    UserCropInterestResponse getUserCropInterest(Long id);
}