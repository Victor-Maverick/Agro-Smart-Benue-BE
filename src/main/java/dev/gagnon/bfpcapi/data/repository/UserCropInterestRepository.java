package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.User;
import dev.gagnon.bfpcapi.data.model.UserCropInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCropInterestRepository extends JpaRepository<UserCropInterest, Long> {
    List<UserCropInterest> findByUser(User user);
    List<UserCropInterest> findByUserId(Long userId);
}