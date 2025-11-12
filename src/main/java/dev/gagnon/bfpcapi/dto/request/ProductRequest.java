package dev.gagnon.bfpcapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private Double unitPrice;
    private Long quantity;
    private String quantityCategory;
    private String location;
    private MultipartFile image;
}
