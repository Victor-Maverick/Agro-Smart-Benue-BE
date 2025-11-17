package dev.gagnon.bfpcapi.service.impl;

import com.cloudinary.Cloudinary;
import dev.gagnon.bfpcapi.data.model.CropTip;
import dev.gagnon.bfpcapi.data.repository.CropTipRepository;
import dev.gagnon.bfpcapi.dto.request.CropTipRequest;
import dev.gagnon.bfpcapi.dto.response.CropTipResponse;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.service.CropTipService;
import dev.gagnon.bfpcapi.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CropTipServiceImpl implements CropTipService {
    
    private final CropTipRepository cropTipRepository;
    private final Cloudinary cloudinary;

    @Override
    public CropTipResponse createCropTip(CropTipRequest request) {
        log.info("Creating crop tip with title: {}", request.getTitle());
        
        List<String> imageUrls = new ArrayList<>();
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile image : request.getImages()) {
                if (!image.isEmpty()) {
                    String imageUrl = ServiceUtils.getMediaUrl(image, cloudinary.uploader());
                    imageUrls.add(imageUrl);
                }
            }
        }
        
        CropTip cropTip = CropTip.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrls(imageUrls)
                .build();
        
        CropTip savedCropTip = cropTipRepository.save(cropTip);
        log.info("Crop tip created successfully with id: {}", savedCropTip.getId());
        
        return mapToResponse(savedCropTip);
    }

    @Override
    public CropTipResponse updateCropTip(Long id, CropTipRequest request) {
        log.info("Updating crop tip with id: {}", id);
        
        CropTip cropTip = cropTipRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Crop tip not found with id: " + id));
        
        cropTip.setTitle(request.getTitle());
        cropTip.setDescription(request.getDescription());
        
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<String> newImageUrls = new ArrayList<>();
            for (MultipartFile image : request.getImages()) {
                if (!image.isEmpty()) {
                    String imageUrl = ServiceUtils.getMediaUrl(image, cloudinary.uploader());
                    newImageUrls.add(imageUrl);
                }
            }
            if (!newImageUrls.isEmpty()) {
                cropTip.setImageUrls(newImageUrls);
            }
        }
        
        CropTip updatedCropTip = cropTipRepository.save(cropTip);
        log.info("Crop tip updated successfully with id: {}", updatedCropTip.getId());
        
        return mapToResponse(updatedCropTip);
    }

    @Override
    public void deleteCropTip(Long id) {
        log.info("Deleting crop tip with id: {}", id);
        
        if (!cropTipRepository.existsById(id)) {
            throw new BusinessException("Crop tip not found with id: " + id);
        }
        
        cropTipRepository.deleteById(id);
        log.info("Crop tip deleted successfully with id: {}", id);
    }

    @Override
    public CropTipResponse getCropTipById(Long id) {
        log.info("Fetching crop tip with id: {}", id);
        
        CropTip cropTip = cropTipRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Crop tip not found with id: " + id));
        
        return mapToResponse(cropTip);
    }

    @Override
    public List<CropTipResponse> getAllCropTips() {
        return cropTipRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private CropTipResponse mapToResponse(CropTip cropTip) {
        return CropTipResponse.builder()
                .id(cropTip.getId())
                .title(cropTip.getTitle())
                .description(cropTip.getDescription())
                .imageUrls(cropTip.getImageUrls())
                .createdAt(cropTip.getCreatedAt())
                .updatedAt(cropTip.getUpdatedAt())
                .build();
    }
}
