package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.dto.request.ProductRequest;
import dev.gagnon.bfpcapi.dto.request.ProductBidRequest;
import dev.gagnon.bfpcapi.dto.request.ProductDemandRequest;
import dev.gagnon.bfpcapi.dto.request.DemandResponseRequest;
import dev.gagnon.bfpcapi.dto.response.ProductResponse;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.exception.BFPCBaseException;
import dev.gagnon.bfpcapi.service.ProductService;
import dev.gagnon.bfpcapi.data.model.ProductBid;
import dev.gagnon.bfpcapi.data.model.ProductDemand;
import dev.gagnon.bfpcapi.data.model.DemandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<?>addProduct(@RequestParam String email, @ModelAttribute ProductRequest request) {
        try{
            String response = productService.addProduct(email,request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(BFPCBaseException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/update")
    public ResponseEntity<?>update(@RequestParam Long id, @ModelAttribute ProductRequest request) {
        try{
            String response = productService.updateProduct(id,request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(BFPCBaseException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<?> getById(@RequestParam Long id) {
        try{
            ProductResponse response = productService.findById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        try{
            List<ProductResponse> products = productService.findAll();
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/by-user")
    public ResponseEntity<?> getProductsByUser(@RequestParam String email) {
        try{
            List<ProductResponse> products = productService.findByUserEmail(email);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, products));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Product Bidding Endpoints
    @PostMapping("/bid")
    public ResponseEntity<?> placeBid(@RequestParam String bidderEmail, @RequestBody ProductBidRequest request) {
        try{
            ProductBid bid = productService.placeBid(bidderEmail, request);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, bid));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/bids/{productId}")
    public ResponseEntity<?> getBidsForProduct(@PathVariable Long productId) {
        try{
            List<ProductBid> bids = productService.getBidsForProduct(productId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, bids));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/bid/{bidId}/accept")
    public ResponseEntity<?> acceptBid(@PathVariable Long bidId) {
        try{
            String response = productService.acceptBid(bidId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/bid/{bidId}/reject")
    public ResponseEntity<?> rejectBid(@PathVariable Long bidId) {
        try{
            String response = productService.rejectBid(bidId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Product Demand Endpoints
    @PostMapping("/demand")
    public ResponseEntity<?> createDemand(@RequestParam String buyerEmail, @RequestBody ProductDemandRequest request) {
        try{
            ProductDemand demand = productService.createDemand(buyerEmail, request);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, demand));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/demands")
    public ResponseEntity<?> getAllDemands() {
        try{
            List<ProductDemand> demands = productService.getAllActiveDemands();
            return ResponseEntity.ok(new BfpcApiResponse<>(true, demands));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/demands/by-user")
    public ResponseEntity<?> getDemandsByUser(@RequestParam String email) {
        try{
            List<ProductDemand> demands = productService.getDemandsByUser(email);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, demands));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Demand Response Endpoints
    @PostMapping("/demand/respond")
    public ResponseEntity<?> respondToDemand(@RequestParam String supplierEmail, @ModelAttribute DemandResponseRequest request) {
        try{
            DemandResponse response = productService.respondToDemand(supplierEmail, request);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/demand/{demandId}/responses")
    public ResponseEntity<?> getResponsesForDemand(@PathVariable Long demandId) {
        try{
            List<DemandResponse> responses = productService.getResponsesForDemand(demandId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, responses));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/demand/response/{responseId}/accept")
    public ResponseEntity<?> acceptDemandResponse(@PathVariable Long responseId) {
        try{
            String response = productService.acceptDemandResponse(responseId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/demand/response/{responseId}/reject")
    public ResponseEntity<?> rejectDemandResponse(@PathVariable Long responseId) {
        try{
            String response = productService.rejectDemandResponse(responseId);
            return ResponseEntity.ok(new BfpcApiResponse<>(true, response));
        }
        catch (BFPCBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
