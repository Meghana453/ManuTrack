package com.manutrack.module.production.repository;

import com.manutrack.module.production.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByPlan_PlanId(Long planId);
    List<WorkOrder> findByMachine_MachineId(Long machineId);
    List<WorkOrder> findByStatus(WorkOrder.Status status);
}
