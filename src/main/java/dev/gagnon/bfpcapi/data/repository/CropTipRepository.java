package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.CropTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CropTipRepository extends JpaRepository<CropTip, Long> {
}
