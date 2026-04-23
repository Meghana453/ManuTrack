package com.manutrack.module.production.repository;

import com.manutrack.module.production.entity.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {
    List<ProductionPlan> findByPlantId(Long plantId);
    List<ProductionPlan> findByStatus(ProductionPlan.Status status);
}
