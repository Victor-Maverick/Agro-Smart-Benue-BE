package dev.gagnon.bfpcapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropTipRequest {
    private String title;
    private String description;
    private List<MultipartFile> images;
}
