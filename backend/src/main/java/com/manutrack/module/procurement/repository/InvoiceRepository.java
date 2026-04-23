package com.manutrack.module.procurement.repository;

import com.manutrack.module.procurement.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPurchaseOrder_PoId(Long poId);
    List<Invoice> findByStatus(Invoice.Status status);
}
