package dev.gagnon.bfpcapi.service.impl;

import com.cloudinary.Cloudinary;
import dev.gagnon.bfpcapi.data.model.*;
import dev.gagnon.bfpcapi.data.repository.*;
import dev.gagnon.bfpcapi.dto.request.*;
import dev.gagnon.bfpcapi.dto.response.ProductResponse;
import dev.gagnon.bfpcapi.exception.BFPCBaseException;
import dev.gagnon.bfpcapi.exception.ResourceNotFoundException;
import dev.gagnon.bfpcapi.service.ProductService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static dev.gagnon.bfpcapi.utils.ServiceUtils.getMediaUrl;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final Cloudinary cloudinary;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductBidRepository productBidRepository;
    private final ProductDemandRepository productDemandRepository;
    private final DemandResponseRepository demandResponseRepository;
    private final CropRepository cropRepository;
    private final ModelMapper modelMapper;

    @Override
    public String addProduct(String email, ProductRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Product> products = productRepository.findAllByUserAndName(email,request.getName(),request.getQuantityCategory());
        if (!products.isEmpty())
            throw new BFPCBaseException("user already added product, update product");
        String imageUrl = getMediaUrl(request.getImage(), cloudinary.uploader());
        Product product = modelMapper.map(request, Product.class);
        product.setUnitPrice(BigDecimal.valueOf(request.getUnitPrice()));
        product.setFarmer(user);
        product.setImageUrl(imageUrl);
        productRepository.save(product);
        return "product added successfully";
    }

    @Override
    public String updateProduct(Long id, ProductRequest request) {
        Product product = getProduct(id);
        
        // Update fields if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getUnitPrice() != null) {
            product.setUnitPrice(BigDecimal.valueOf(request.getUnitPrice()));
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getQuantityCategory() != null) {
            product.setQuantityCategory(request.getQuantityCategory());
        }
        if (request.getLocation() != null) {
            product.setLocation(request.getLocation());
        }
        
        // Update image only if provided (optional)
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = getMediaUrl(request.getImage(), cloudinary.uploader());
            product.setImageUrl(imageUrl);
        }
        
        productRepository.save(product);
        return "Product updated successfully";
    }

    @Override
    public ProductResponse findById(Long id) {
        return new ProductResponse(getProduct(id));
    }

    @Override
    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findByIsAvailableTrue();
        return products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> findByUserEmail(String email) {
        User user = getUserByEmail(email);
        List<Product> products = productRepository.findByFarmer(user);
        return products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    // Bidding methods
    @Override
    @Transactional
    public ProductBid placeBid(String bidderEmail, ProductBidRequest request) {
        User bidder = getUserByEmail(bidderEmail);
        Product product = getProduct(request.getProductId());
        
        if (!product.isAvailable()) {
            throw new BFPCBaseException("Product is no longer available for bidding");
        }
        
        ProductBid bid = ProductBid.builder()
                .bidPrice(request.getBidPrice())
                .quantity(request.getQuantity())
                .message(request.getMessage())
                .phoneContact(request.getPhoneContact())
                .location(request.getLocation())
                .product(product)
                .bidder(bidder)
                .build();
                
        return productBidRepository.save(bid);
    }

    @Override
    public List<ProductBid> getBidsForProduct(Long productId) {
        Product product = getProduct(productId);
        return productBidRepository.findByProduct(product);
    }

    @Override
    @Transactional
    public String acceptBid(Long bidId) {
        ProductBid bid = productBidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
                
        if (bid.getStatus() != ProductBid.BidStatus.PENDING) throw new BFPCBaseException("Bid is no longer pending");

        // Accept the bid
        bid.setStatus(ProductBid.BidStatus.ACCEPTED);
        productBidRepository.save(bid);
        
        // Mark product as unavailable
        Product product = bid.getProduct();
        product.setAvailable(false);
        productRepository.save(product);
        
        // Reject all other pending bids for this product
        List<ProductBid> otherBids = productBidRepository.findByProductAndStatus(product, ProductBid.BidStatus.PENDING);
        otherBids.forEach(otherBid -> {
            if (!otherBid.getId().equals(bidId)) {
                otherBid.setStatus(ProductBid.BidStatus.REJECTED);
            }
        });
        productBidRepository.saveAll(otherBids);
        return "Bid accepted successfully. Product is no longer available sold.";
    }

    @Override
    public String rejectBid(Long bidId) {
        ProductBid bid = productBidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
                
        if (bid.getStatus() != ProductBid.BidStatus.PENDING) {
            throw new BFPCBaseException("Bid is no longer pending");
        }
        
        bid.setStatus(ProductBid.BidStatus.REJECTED);
        productBidRepository.save(bid);
        
        return "Bid rejected successfully";
    }

    // Demand methods
    @Override
    public ProductDemand createDemand(String buyerEmail, ProductDemandRequest request) {
        User buyer = getUserByEmail(buyerEmail);
        
        ProductDemand.ProductDemandBuilder demandBuilder = ProductDemand.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .offerPrice(request.getOfferPrice())
                .quantity(request.getQuantity())
                .quantityCategory(request.getQuantityCategory())
                .location(request.getLocation())
                .phoneContact(request.getPhoneContact())
                .buyer(buyer);
        
        // Add crop if specified
        if (request.getCropId() != null) {
            Crop crop = cropRepository.findById(request.getCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop not found"));
            demandBuilder.crop(crop);
        }
        
        ProductDemand demand = demandBuilder.build();
        return productDemandRepository.save(demand);
    }

    @Override
    public List<ProductDemand> getAllActiveDemands() {
        return productDemandRepository.findByIsActiveTrue();
    }

    @Override
    public List<ProductDemand> getDemandsByUser(String email) {
        User user = getUserByEmail(email);
        return productDemandRepository.findByBuyer(user);
    }

    // Demand response methods
    @Override
    public DemandResponse respondToDemand(String supplierEmail, DemandResponseRequest request) {
        User supplier = getUserByEmail(supplierEmail);
        ProductDemand demand = productDemandRepository.findById(request.getDemandId())
                .orElseThrow(() -> new ResourceNotFoundException("Demand not found"));
        
        if (!demand.isActive()) {
            throw new BFPCBaseException("Demand is no longer active");
        }
        
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = getMediaUrl(request.getImage(), cloudinary.uploader());
        }
        
        DemandResponse response = DemandResponse.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .offerPrice(request.getOfferPrice())
                .availableQuantity(request.getAvailableQuantity())
                .quantityCategory(request.getQuantityCategory())
                .location(request.getLocation())
                .phoneContact(request.getPhoneContact())
                .imageUrl(imageUrl)
                .demand(demand)
                .supplier(supplier)
                .build();
                
        return demandResponseRepository.save(response);
    }

    @Override
    public List<DemandResponse> getResponsesForDemand(Long demandId) {
        ProductDemand demand = productDemandRepository.findById(demandId)
                .orElseThrow(() -> new ResourceNotFoundException("Demand not found"));
        return demandResponseRepository.findByDemand(demand);
    }

    @Override
    @Transactional
    public String acceptDemandResponse(Long responseId) {
        DemandResponse response = demandResponseRepository.findById(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found"));
                
        if (response.getStatus() != DemandResponse.ResponseStatus.PENDING) {
            throw new BFPCBaseException("Response is no longer pending");
        }
        
        // Accept the response
        response.setStatus(DemandResponse.ResponseStatus.ACCEPTED);
        demandResponseRepository.save(response);
        
        // Mark demand as inactive
        ProductDemand demand = response.getDemand();
        demand.setActive(false);
        productDemandRepository.save(demand);
        
        // Reject all other pending responses for this demand
        List<DemandResponse> otherResponses = demandResponseRepository.findByDemand(demand);
        otherResponses.forEach(otherResponse -> {
            if (!otherResponse.getId().equals(responseId) && 
                otherResponse.getStatus() == DemandResponse.ResponseStatus.PENDING) {
                otherResponse.setStatus(DemandResponse.ResponseStatus.REJECTED);
            }
        });
        demandResponseRepository.saveAll(otherResponses);
        return "Response accepted successfully. Demand is now fulfilled.";
    }

    @Override
    public String rejectDemandResponse(Long responseId) {
        DemandResponse response = demandResponseRepository.findById(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found"));
                
        if (response.getStatus() != DemandResponse.ResponseStatus.PENDING) {
            throw new BFPCBaseException("Response is no longer pending");
        }
        
        response.setStatus(DemandResponse.ResponseStatus.REJECTED);
        demandResponseRepository.save(response);
        
        return "Response rejected successfully";
    }

    // Helper methods
    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
