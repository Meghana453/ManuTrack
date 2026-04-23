package com.manutrack.module.procurement.repository;

import com.manutrack.module.procurement.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByVendor_VendorId(Long vendorId);
    List<PurchaseOrder> findByStatus(PurchaseOrder.Status status);
}
