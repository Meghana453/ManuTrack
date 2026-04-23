package com.manutrack.module.logistics.repository;

import com.manutrack.module.logistics.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    List<Carrier> findByStatus(Carrier.Status status);
}
