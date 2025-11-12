package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.model.UserProfile;
import dev.gagnon.bfpcapi.data.repository.UserProfileRepository;
import dev.gagnon.bfpcapi.data.repository.UserRepository;
import dev.gagnon.bfpcapi.dto.request.UserProfileRequest;
import dev.gagnon.bfpcapi.exception.UserNotFoundException;
import dev.gagnon.bfpcapi.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Override
    public UserProfile createOrUpdateProfile(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(UserProfile.builder().user(user).build());

        profile.setLocation(request.getLocation());
        profile.setLga(request.getLga());
        profile.setState(request.getState());
        profile.setFarmingExperience(request.getFarmingExperience());
        profile.setPrimaryCrop(request.getPrimaryCrop());
        profile.setFarmSize(request.getFarmSize());
        profile.setFarmingType(request.getFarmingType());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());
        profile.setProfileCompleted(true);

        return userProfileRepository.save(profile);
    }

    @Override
    public UserProfile getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public boolean hasCompletedProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(UserProfile::isProfileCompleted)
                .orElse(false);
    }
}