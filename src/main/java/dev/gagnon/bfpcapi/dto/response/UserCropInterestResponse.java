package dev.gagnon.bfpcapi.dto.response;

import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.model.UserCropInterest;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCropInterestResponse {
    private Long id;
    private String farmerName;
    private Long cropId;
    private String name;
    private String category; // grains, tubers, vegetables, etc.
    private String description;
    private String plantingSeason;
    private String harvestSeason;
    private Integer growthPeriodDays;
    private String imageUrl;
    public UserCropInterestResponse(UserCropInterest cropInterest) {
        this.id = cropInterest.getId();
        this.farmerName = cropInterest.getUser().getFirstName()+" "+cropInterest.getUser().getLastName();
        this.cropId = cropInterest.getCrop().getId();
        this.name = cropInterest.getCrop().getName();
        this.category = cropInterest.getCrop().getCategory();
        this.description = cropInterest.getCrop().getDescription();
        this.plantingSeason = cropInterest.getCrop().getPlantingSeason();
        this.harvestSeason = cropInterest.getCrop().getHarvestSeason();
        this.growthPeriodDays = cropInterest.getCrop().getGrowthPeriodDays();
        this.imageUrl = cropInterest.getCrop().getImageUrl();

    }
}
