package dev.gagnon.bfpcapi.dto.request;

import lombok.Data;

@Data
public class FarmRequest {
    private String farmName;
    private String location;
    private String coordinates;
    private Double size;
    private String soilType;
    private String description;
}