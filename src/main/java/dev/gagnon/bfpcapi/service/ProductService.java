package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.dto.request.ProductRequest;
import dev.gagnon.bfpcapi.dto.request.ProductBidRequest;
import dev.gagnon.bfpcapi.dto.request.ProductDemandRequest;
import dev.gagnon.bfpcapi.dto.request.DemandResponseRequest;
import dev.gagnon.bfpcapi.dto.response.ProductDemandResponse;
import dev.gagnon.bfpcapi.dto.response.ProductResponse;
import dev.gagnon.bfpcapi.data.model.ProductBid;
import dev.gagnon.bfpcapi.data.model.ProductDemand;
import dev.gagnon.bfpcapi.data.model.DemandResponse;

import java.util.List;

public interface ProductService {
    String addProduct(String email, ProductRequest request);

    String updateProduct(Long id, ProductRequest request);

    ProductResponse findById(Long id);

    List<ProductResponse> findAll();
    
    List<ProductResponse> findByUserEmail(String email);

    // Bidding methods
    ProductBid placeBid(String bidderEmail, ProductBidRequest request);
    
    List<ProductBid> getBidsForProduct(Long productId);
    
    String acceptBid(Long bidId);
    
    String rejectBid(Long bidId);

    // Demand methods
    String createDemand(String buyerEmail, ProductDemandRequest request);
    
    List<ProductDemandResponse> getAllActiveDemands();
    
    List<ProductDemandResponse> getDemandsByUser(String email);

    // Demand response methods
    DemandResponse respondToDemand(String supplierEmail, DemandResponseRequest request);
    
    List<DemandResponse> getResponsesForDemand(Long demandId);
    
    String acceptDemandResponse(Long responseId);
    
    String rejectDemandResponse(Long responseId);

    ProductDemandResponse getDemandsById(Long id);

    List<ProductResponse> findAllAvailable();
}
