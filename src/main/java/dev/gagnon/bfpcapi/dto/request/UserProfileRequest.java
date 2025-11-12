package dev.gagnon.bfpcapi.dto.request;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String location;
    private String lga;
    private String state;
    private String farmingExperience;
    private String primaryCrop;
    private Double farmSize;
    private String farmingType;
    private String profilePictureUrl;
}