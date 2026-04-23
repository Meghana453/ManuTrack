package com.manutrack.module.logistics.repository;

import com.manutrack.module.logistics.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByStatus(Shipment.Status status);
    List<Shipment> findByCarrier_CarrierId(Long carrierId);
}
