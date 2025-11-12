package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
