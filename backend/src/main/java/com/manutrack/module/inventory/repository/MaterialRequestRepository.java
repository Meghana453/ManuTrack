package com.manutrack.module.inventory.repository;

import com.manutrack.module.inventory.entity.MaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Long> {
    List<MaterialRequest> findByWorkOrderId(Long workOrderId);
    List<MaterialRequest> findByStatus(MaterialRequest.Status status);
    List<MaterialRequest> findByItem_ItemId(Long itemId);
}
