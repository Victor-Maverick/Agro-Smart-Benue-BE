package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.DemandResponse;
import dev.gagnon.bfpcapi.data.model.ProductDemand;
import dev.gagnon.bfpcapi.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandResponseRepository extends JpaRepository<DemandResponse, Long> {
    List<DemandResponse> findByDemand(ProductDemand demand);
    List<DemandResponse> findBySupplier(User supplier);
    List<DemandResponse> findByStatus(DemandResponse.ResponseStatus status);
}