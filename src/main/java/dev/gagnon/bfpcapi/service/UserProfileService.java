package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.UserProfile;
import dev.gagnon.bfpcapi.dto.request.UserProfileRequest;

public interface UserProfileService {
    UserProfile createOrUpdateProfile(Long userId, UserProfileRequest request);
    UserProfile getProfileByUserId(Long userId);
    boolean hasCompletedProfile(Long userId);
}