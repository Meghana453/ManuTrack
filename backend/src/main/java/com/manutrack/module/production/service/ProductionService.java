//////package com.manutrack.module.production.service;
//////
//////import com.manutrack.automation.AutomationService;
//////import com.manutrack.exception.BusinessException;
//////import com.manutrack.exception.ResourceNotFoundException;
//////import com.manutrack.module.production.dto.ProductionDtos;
//////import com.manutrack.module.production.entity.Machine;
//////import com.manutrack.module.production.entity.ProductionPlan;
//////import com.manutrack.module.production.entity.WorkOrder;
//////import com.manutrack.module.production.mapper.ProductionMapper;
//////import com.manutrack.module.production.repository.MachineRepository;
//////import com.manutrack.module.production.repository.ProductionPlanRepository;
//////import com.manutrack.module.production.repository.WorkOrderRepository;
//////import lombok.RequiredArgsConstructor;
//////import lombok.extern.slf4j.Slf4j;
//////import org.springframework.stereotype.Service;
//////import org.springframework.transaction.annotation.Transactional;
//////import java.time.LocalDateTime;
//////import java.util.List;
//////
//////@Service @RequiredArgsConstructor @Transactional @Slf4j
//////public class ProductionService {
//////    private final ProductionPlanRepository planRepo;
//////    private final MachineRepository machineRepo;
//////    private final WorkOrderRepository workOrderRepo;
//////    private final ProductionMapper mapper;
//////    private final AutomationService automationService;
//////
//////    public ProductionDtos.PlanResponse createPlan(ProductionDtos.CreatePlanRequest req) {
//////        if (req.getEndDate().isBefore(req.getStartDate())) throw new BusinessException("End date must be after start date");
//////        ProductionPlan plan = mapper.toPlan(req);
//////        if (req.getStatus() != null) plan.setStatus(req.getStatus());
//////        return mapper.toPlanResponse(planRepo.save(plan));
//////    }
//////    @Transactional(readOnly=true) public List<ProductionDtos.PlanResponse> getAllPlans() { return planRepo.findAll().stream().map(mapper::toPlanResponse).toList(); }
//////    @Transactional(readOnly=true) public ProductionDtos.PlanResponse getPlanById(Long id) { return mapper.toPlanResponse(findPlanById(id)); }
//////    public ProductionDtos.PlanResponse updatePlan(Long id, ProductionDtos.UpdatePlanRequest req) { ProductionPlan p = findPlanById(id); mapper.updatePlan(req, p); return mapper.toPlanResponse(planRepo.save(p)); }
//////    public void deletePlan(Long id) { findPlanById(id); planRepo.deleteById(id); }
//////
//////    public ProductionDtos.MachineResponse createMachine(ProductionDtos.CreateMachineRequest req) {
//////        Machine m = mapper.toMachine(req); if (req.getStatus() != null) m.setStatus(req.getStatus()); return mapper.toMachineResponse(machineRepo.save(m));
//////    }
//////    @Transactional(readOnly=true) public List<ProductionDtos.MachineResponse> getAllMachines() { return machineRepo.findAll().stream().map(mapper::toMachineResponse).toList(); }
//////    @Transactional(readOnly=true) public ProductionDtos.MachineResponse getMachineById(Long id) { return mapper.toMachineResponse(findMachineById(id)); }
//////
//////    public ProductionDtos.MachineResponse updateMachine(Long id, ProductionDtos.UpdateMachineRequest req) {
//////        Machine machine = findMachineById(id);
//////        Machine.Status prev = machine.getStatus();
//////        mapper.updateMachine(req, machine);
//////        machine = machineRepo.save(machine);
//////        if (req.getStatus() != null && req.getStatus() != prev) {
//////            if (req.getStatus() == Machine.Status.MAINTENANCE) automationService.onMachineSetToMaintenance(machine);
//////            else if (req.getStatus() == Machine.Status.ACTIVE && prev == Machine.Status.MAINTENANCE) automationService.onMachineSetToActive(machine);
//////        }
//////        return mapper.toMachineResponse(machine);
//////    }
//////    public void deleteMachine(Long id) { findMachineById(id); machineRepo.deleteById(id); }
//////
//////    public ProductionDtos.WorkOrderResponse createWorkOrder(ProductionDtos.CreateWorkOrderRequest req) {
//////        ProductionPlan plan = findPlanById(req.getPlanId());
//////        WorkOrder wo = WorkOrder.builder().plan(plan).productId(req.getProductId()).quantity(req.getQuantity())
//////                .scheduledStart(req.getScheduledStart()).scheduledEnd(req.getScheduledEnd())
//////                .status(req.getStatus() != null ? req.getStatus() : WorkOrder.Status.PENDING).build();
//////        if (req.getMachineId() != null) wo.setMachine(findMachineById(req.getMachineId()));
//////        return mapper.toWorkOrderResponse(workOrderRepo.save(wo));
//////    }
//////    @Transactional(readOnly=true) public List<ProductionDtos.WorkOrderResponse> getAllWorkOrders() { return workOrderRepo.findAll().stream().map(mapper::toWorkOrderResponse).toList(); }
//////    @Transactional(readOnly=true) public ProductionDtos.WorkOrderResponse getWorkOrderById(Long id) { return mapper.toWorkOrderResponse(findWorkOrderById(id)); }
//////    @Transactional(readOnly=true) public List<ProductionDtos.WorkOrderResponse> getWorkOrdersByPlan(Long planId) { return workOrderRepo.findByPlan_PlanId(planId).stream().map(mapper::toWorkOrderResponse).toList(); }
//////
//////    public ProductionDtos.WorkOrderResponse updateWorkOrder(Long id, ProductionDtos.UpdateWorkOrderRequest req) {
//////        WorkOrder wo = findWorkOrderById(id);
//////        WorkOrder.Status prev = wo.getStatus();
//////        mapper.updateWorkOrder(req, wo);
//////        if (req.getStatus() == WorkOrder.Status.IN_PROGRESS && wo.getActualStart() == null) wo.setActualStart(LocalDateTime.now());
//////        if (req.getStatus() == WorkOrder.Status.COMPLETED && wo.getActualEnd() == null) wo.setActualEnd(LocalDateTime.now());
//////        if (req.getMachineId() != null) wo.setMachine(findMachineById(req.getMachineId()));
//////        wo = workOrderRepo.save(wo);
//////        if (req.getStatus() != null && req.getStatus() != prev) {
//////            if (req.getStatus() == WorkOrder.Status.IN_PROGRESS) automationService.onWorkOrderStarted(wo);
//////            else if (req.getStatus() == WorkOrder.Status.COMPLETED) automationService.onWorkOrderCompleted(wo);
//////            else if (req.getStatus() == WorkOrder.Status.HALTED) automationService.onWorkOrderHalted(wo);
//////        }
//////        return mapper.toWorkOrderResponse(wo);
//////    }
//////    public void deleteWorkOrder(Long id) { findWorkOrderById(id); workOrderRepo.deleteById(id); }
//////
//////    private ProductionPlan findPlanById(Long id) { return planRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("ProductionPlan","id",id)); }
//////    private Machine findMachineById(Long id) { return machineRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Machine","id",id)); }
//////    private WorkOrder findWorkOrderById(Long id) { return workOrderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("WorkOrder","id",id)); }
//////}
////package com.manutrack.module.production.service;
////
////import com.manutrack.automation.AutomationService;
////import com.manutrack.exception.BusinessException;
////import com.manutrack.exception.ResourceNotFoundException;
////import com.manutrack.module.production.dto.ProductionDtos;
////import com.manutrack.module.production.entity.Machine;
////import com.manutrack.module.production.entity.ProductionPlan;
////import com.manutrack.module.production.entity.WorkOrder;
////import com.manutrack.module.production.mapper.ProductionMapper;
////import com.manutrack.module.production.repository.MachineRepository;
////import com.manutrack.module.production.repository.ProductionPlanRepository;
////import com.manutrack.module.production.repository.WorkOrderRepository;
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////import java.time.LocalDateTime;
////import java.util.List;
////
////@Service @RequiredArgsConstructor @Transactional @Slf4j
////public class ProductionService {
////    private final ProductionPlanRepository planRepo;
////    private final com.manutrack.module.inventory.repository.InventoryItemRepository inventoryItemRepo;
////    private final MachineRepository machineRepo;
////    private final WorkOrderRepository workOrderRepo;
////    private final ProductionMapper mapper;
////    private final AutomationService automationService;
////
////    public ProductionDtos.PlanResponse createPlan(ProductionDtos.CreatePlanRequest req) {
////        if (req.getEndDate().isBefore(req.getStartDate())) throw new BusinessException("End date must be after start date");
////        ProductionPlan plan = mapper.toPlan(req);
////        if (req.getStatus() != null) plan.setStatus(req.getStatus());
////        return mapper.toPlanResponse(planRepo.save(plan));
////    }
////    @Transactional(readOnly=true) public List<ProductionDtos.PlanResponse> getAllPlans() { return planRepo.findAll().stream().map(mapper::toPlanResponse).toList(); }
////    @Transactional(readOnly=true) public ProductionDtos.PlanResponse getPlanById(Long id) { return mapper.toPlanResponse(findPlanById(id)); }
////    public ProductionDtos.PlanResponse updatePlan(Long id, ProductionDtos.UpdatePlanRequest req) { ProductionPlan p = findPlanById(id); mapper.updatePlan(req, p); return mapper.toPlanResponse(planRepo.save(p)); }
////    public void deletePlan(Long id) { findPlanById(id); planRepo.deleteById(id); }
////
////    public ProductionDtos.MachineResponse createMachine(ProductionDtos.CreateMachineRequest req) {
////        Machine m = mapper.toMachine(req); if (req.getStatus() != null) m.setStatus(req.getStatus()); return mapper.toMachineResponse(machineRepo.save(m));
////    }
////    @Transactional(readOnly=true) public List<ProductionDtos.MachineResponse> getAllMachines() { return machineRepo.findAll().stream().map(mapper::toMachineResponse).toList(); }
////    @Transactional(readOnly=true) public ProductionDtos.MachineResponse getMachineById(Long id) { return mapper.toMachineResponse(findMachineById(id)); }
////
////    public ProductionDtos.MachineResponse updateMachine(Long id, ProductionDtos.UpdateMachineRequest req) {
////        Machine machine = findMachineById(id);
////        Machine.Status prev = machine.getStatus();
////        mapper.updateMachine(req, machine);
////        machine = machineRepo.save(machine);
////        if (req.getStatus() != null && req.getStatus() != prev) {
////            if (req.getStatus() == Machine.Status.MAINTENANCE) automationService.onMachineSetToMaintenance(machine);
////            else if (req.getStatus() == Machine.Status.ACTIVE && prev == Machine.Status.MAINTENANCE) automationService.onMachineSetToActive(machine);
////        }
////        return mapper.toMachineResponse(machine);
////    }
////    public void deleteMachine(Long id) { findMachineById(id); machineRepo.deleteById(id); }
////
////    public ProductionDtos.WorkOrderResponse createWorkOrder(ProductionDtos.CreateWorkOrderRequest req) {
////        ProductionPlan plan = findPlanById(req.getPlanId());
////        WorkOrder wo = WorkOrder.builder().plan(plan).productId(req.getProductId()).quantity(req.getQuantity())
////                .scheduledStart(req.getScheduledStart()).scheduledEnd(req.getScheduledEnd())
////                .status(req.getStatus() != null ? req.getStatus() : WorkOrder.Status.PENDING).build();
////        if (req.getMachineId() != null) wo.setMachine(findMachineById(req.getMachineId()));
////        return mapper.toWorkOrderResponse(workOrderRepo.save(wo));
////    }
////    @Transactional(readOnly=true) public List<ProductionDtos.WorkOrderResponse> getAllWorkOrders() { return workOrderRepo.findAll().stream().map(mapper::toWorkOrderResponse).toList(); }
////    @Transactional(readOnly=true) public ProductionDtos.WorkOrderResponse getWorkOrderById(Long id) { return mapper.toWorkOrderResponse(findWorkOrderById(id)); }
////    @Transactional(readOnly=true) public List<ProductionDtos.WorkOrderResponse> getWorkOrdersByPlan(Long planId) { return workOrderRepo.findByPlan_PlanId(planId).stream().map(mapper::toWorkOrderResponse).toList(); }
////
////    public ProductionDtos.WorkOrderResponse updateWorkOrder(Long id, ProductionDtos.UpdateWorkOrderRequest req) {
////        WorkOrder wo = findWorkOrderById(id);
////        WorkOrder.Status prev = wo.getStatus();
////        mapper.updateWorkOrder(req, wo);
////        if (req.getStatus() == WorkOrder.Status.IN_PROGRESS && wo.getActualStart() == null) wo.setActualStart(LocalDateTime.now());
////        if (req.getStatus() == WorkOrder.Status.COMPLETED && wo.getActualEnd() == null) wo.setActualEnd(LocalDateTime.now());
////        if (req.getMachineId() != null) wo.setMachine(findMachineById(req.getMachineId()));
////        wo = workOrderRepo.save(wo);
////        if (req.getStatus() != null && req.getStatus() != prev) {
////            if (req.getStatus() == WorkOrder.Status.IN_PROGRESS) automationService.onWorkOrderStarted(wo);
////            else if (req.getStatus() == WorkOrder.Status.COMPLETED) automationService.onWorkOrderCompleted(wo);
////            else if (req.getStatus() == WorkOrder.Status.HALTED) automationService.onWorkOrderHalted(wo);
////        }
////        return mapper.toWorkOrderResponse(wo);
////    }
////    public void deleteWorkOrder(Long id) { findWorkOrderById(id); workOrderRepo.deleteById(id); }
////
////    private ProductionPlan findPlanById(Long id) { return planRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("ProductionPlan","id",id)); }
////    private Machine findMachineById(Long id) { return machineRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Machine","id",id)); }
////    private WorkOrder findWorkOrderById(Long id) { return workOrderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("WorkOrder","id",id)); }
////}
//package com.manutrack.module.production.service;
//import com.manutrack.automation.AutomationService;
//import com.manutrack.exception.BusinessException;
//import com.manutrack.exception.ResourceNotFoundException;
//import com.manutrack.module.inventory.entity.MaterialRequest;
//import com.manutrack.module.inventory.repository.MaterialRequestRepository;
//import com.manutrack.module.production.dto.ProductionDtos;
//import com.manutrack.module.production.entity.Machine;
//import com.manutrack.module.production.entity.ProductionPlan;
//import com.manutrack.module.production.entity.WorkOrder;
//import com.manutrack.module.production.mapper.ProductionMapper;
//import com.manutrack.module.production.repository.MachineRepository;
//import com.manutrack.module.production.repository.ProductionPlanRepository;
//import com.manutrack.module.production.repository.WorkOrderRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service @RequiredArgsConstructor @Transactional @Slf4j
//public class ProductionService {
//
//    private final ProductionPlanRepository planRepo;
//    private final MachineRepository machineRepo;
//    private final WorkOrderRepository workOrderRepo;
//    private final ProductionMapper mapper;
//    private final AutomationService automationService;
//    // Added: to link WO IDs to existing PENDING material requests
//    private final MaterialRequestRepository materialRequestRepo;
//
//    public ProductionDtos.PlanResponse createPlan(ProductionDtos.CreatePlanRequest req) {
//        if (req.getEndDate().isBefore(req.getStartDate())) throw new
//                BusinessException("End date must be after start date");
//        ProductionPlan plan = mapper.toPlan(req);
//        if (req.getStatus() != null) plan.setStatus(req.getStatus());
//        return mapper.toPlanResponse(planRepo.save(plan));
//    }
//
//    @Transactional(readOnly=true) public List<ProductionDtos.PlanResponse>
//    getAllPlans() { return
//            planRepo.findAll().stream().map(mapper::toPlanResponse).toList(); }
//
//    @Transactional(readOnly=true) public ProductionDtos.PlanResponse
//    getPlanById(Long id) { return mapper.toPlanResponse(findPlanById(id)); }
//
//    public ProductionDtos.PlanResponse updatePlan(Long id,
//                                                  ProductionDtos.UpdatePlanRequest req) { ProductionPlan p = findPlanById(id);
//        mapper.updatePlan(req, p); return mapper.toPlanResponse(planRepo.save(p)); }
//
//    public void deletePlan(Long id) { findPlanById(id); planRepo.deleteById(id); }
//
//    public ProductionDtos.MachineResponse
//    createMachine(ProductionDtos.CreateMachineRequest req) {
//        Machine m = mapper.toMachine(req); if (req.getStatus() != null)
//            m.setStatus(req.getStatus()); return
//                mapper.toMachineResponse(machineRepo.save(m));
//    }
//
//    @Transactional(readOnly=true) public List<ProductionDtos.MachineResponse>
//    getAllMachines() { return
//            machineRepo.findAll().stream().map(mapper::toMachineResponse).toList(); }
//
//    @Transactional(readOnly=true) public ProductionDtos.MachineResponse
//    getMachineById(Long id) { return mapper.toMachineResponse(findMachineById(id)); }
//
//    public ProductionDtos.MachineResponse updateMachine(Long id,
//                                                        ProductionDtos.UpdateMachineRequest req) {
//        Machine machine = findMachineById(id);
//        Machine.Status prev = machine.getStatus();
//        mapper.updateMachine(req, machine);
//        machine = machineRepo.save(machine);
//        if (req.getStatus() != null && req.getStatus() != prev) {
//            if (req.getStatus() == Machine.Status.MAINTENANCE)
//                automationService.onMachineSetToMaintenance(machine);
//            else if (req.getStatus() == Machine.Status.ACTIVE && prev ==
//                    Machine.Status.MAINTENANCE)
//                automationService.onMachineSetToActive(machine);
//        }
//        return mapper.toMachineResponse(machine);
//    }
//
//    public void deleteMachine(Long id) { findMachineById(id);
//        machineRepo.deleteById(id); }
//
//    public ProductionDtos.WorkOrderResponse
//    createWorkOrder(ProductionDtos.CreateWorkOrderRequest req) {
//        ProductionPlan plan = findPlanById(req.getPlanId());
//        WorkOrder wo = WorkOrder.builder().plan(plan).productId(req.getProductId()).quantity(req.getQuantity())
//                .scheduledStart(req.getScheduledStart()).scheduledEnd(req.getScheduledEnd())
//                .status(req.getStatus() != null ? req.getStatus() :
//                        WorkOrder.Status.PENDING).build();
//        if (req.getMachineId() != null)
//            wo.setMachine(findMachineById(req.getMachineId()));
//        wo = workOrderRepo.save(wo);
//
//        // Link this new WO's ID to any PENDING material requests that have no WO assigned yet
//        linkWorkOrderToMaterialRequests(wo);
//
//        return mapper.toWorkOrderResponse(wo);
//    }
//
//    @Transactional(readOnly=true) public List<ProductionDtos.WorkOrderResponse>
//    getAllWorkOrders() { return
//            workOrderRepo.findAll().stream().map(mapper::toWorkOrderResponse).toList(); }
//
//    @Transactional(readOnly=true) public ProductionDtos.WorkOrderResponse
//    getWorkOrderById(Long id) { return
//            mapper.toWorkOrderResponse(findWorkOrderById(id)); }
//
//    @Transactional(readOnly=true) public List<ProductionDtos.WorkOrderResponse>
//    getWorkOrdersByPlan(Long planId) { return
//            workOrderRepo.findByPlan_PlanId(planId).stream().map(mapper::toWorkOrderResponse).toList(); }
//
//    public ProductionDtos.WorkOrderResponse updateWorkOrder(Long id,
//                                                            ProductionDtos.UpdateWorkOrderRequest req) {
//        WorkOrder wo = findWorkOrderById(id);
//        WorkOrder.Status prev = wo.getStatus();
//        mapper.updateWorkOrder(req, wo);
//        if (req.getStatus() == WorkOrder.Status.IN_PROGRESS && wo.getActualStart() ==
//                null) wo.setActualStart(LocalDateTime.now());
//        if (req.getStatus() == WorkOrder.Status.COMPLETED && wo.getActualEnd() ==
//                null) wo.setActualEnd(LocalDateTime.now());
//        if (req.getMachineId() != null)
//            wo.setMachine(findMachineById(req.getMachineId()));
//        wo = workOrderRepo.save(wo);
//        if (req.getStatus() != null && req.getStatus() != prev) {
//            if (req.getStatus() == WorkOrder.Status.IN_PROGRESS)
//                automationService.onWorkOrderStarted(wo);
//            else if (req.getStatus() == WorkOrder.Status.COMPLETED)
//                automationService.onWorkOrderCompleted(wo);
//            else if (req.getStatus() == WorkOrder.Status.HALTED)
//                automationService.onWorkOrderHalted(wo);
//        }
//        return mapper.toWorkOrderResponse(wo);
//    }
//
//    public void deleteWorkOrder(Long id) { findWorkOrderById(id);
//        workOrderRepo.deleteById(id); }
//
//    /**
//     * After a WorkOrder is saved, find any PENDING material requests that have
//     * no workOrderId yet and assign this WO's ID to them.
//     * This ensures the Work Order column in Inventory > Material Requests is populated.
//     */
//    private void linkWorkOrderToMaterialRequests(WorkOrder wo) {
//        try {
//            List<MaterialRequest> unlinked = materialRequestRepo.findAll().stream()
//                    .filter(mr -> mr.getWorkOrderId() == null
//                            && mr.getStatus() == MaterialRequest.Status.PENDING)
//                    .toList();
//            if (!unlinked.isEmpty()) {
//                // Assign the most recent unlinked PENDING request to this WO
//                MaterialRequest mr = unlinked.get(unlinked.size() - 1);
//                mr.setWorkOrderId(wo.getWorkOrderId());
//                materialRequestRepo.save(mr);
//                log.info("[AUTO] Linked WO-{} to Material Request #{}",
//                        wo.getWorkOrderId(), mr.getRequestId());
//            }
//        } catch (Exception e) {
//            log.warn("[AUTO] Could not link WO-{} to material request: {}",
//                    wo.getWorkOrderId(), e.getMessage());
//        }
//    }
//
//    private ProductionPlan findPlanById(Long id) { return
//            planRepo.findById(id).orElseThrow(() -> new
//                    ResourceNotFoundException("ProductionPlan","id",id)); }
//
//    private Machine findMachineById(Long id) { return
//            machineRepo.findById(id).orElseThrow(() -> new
//                    ResourceNotFoundException("Machine","id",id)); }
//
//    private WorkOrder findWorkOrderById(Long id) { return
//            workOrderRepo.findById(id).orElseThrow(() -> new
//                    ResourceNotFoundException("WorkOrder","id",id)); }
//}
package com.manutrack.module.production.service;
import com.manutrack.automation.AutomationService;
import com.manutrack.exception.BusinessException;
import com.manutrack.exception.ResourceNotFoundException;
import com.manutrack.module.inventory.entity.MaterialRequest;
import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.repository.InventoryItemRepository;
import com.manutrack.module.inventory.repository.MaterialRequestRepository;
import com.manutrack.module.production.dto.ProductionDtos;
import com.manutrack.module.production.entity.Machine;
import com.manutrack.module.production.entity.ProductionPlan;
import com.manutrack.module.production.entity.WorkOrder;
import com.manutrack.module.production.mapper.ProductionMapper;
import com.manutrack.module.production.repository.MachineRepository;
import com.manutrack.module.production.repository.ProductionPlanRepository;
import com.manutrack.module.production.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class ProductionService {

    private final ProductionPlanRepository   planRepo;
    private final MachineRepository          machineRepo;
    private final WorkOrderRepository        workOrderRepo;
    private final ProductionMapper           mapper;
    private final AutomationService          automationService;
    private final MaterialRequestRepository  materialRequestRepo;
    private final InventoryItemRepository    inventoryItemRepo;

    // ── Production Plans ─────────────────────────────────────

    public ProductionDtos.PlanResponse createPlan(ProductionDtos.CreatePlanRequest req) {
        if (req.getEndDate().isBefore(req.getStartDate()))
            throw new BusinessException("End date must be after start date");
        ProductionPlan plan = mapper.toPlan(req);
        if (req.getStatus() != null) plan.setStatus(req.getStatus());
        return mapper.toPlanResponse(planRepo.save(plan));
    }

    @Transactional(readOnly = true)
    public List<ProductionDtos.PlanResponse> getAllPlans() {
        return planRepo.findAll().stream().map(mapper::toPlanResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductionDtos.PlanResponse getPlanById(Long id) {
        return mapper.toPlanResponse(findPlanById(id));
    }

//    public ProductionDtos.PlanResponse updatePlan(Long id, ProductionDtos.UpdatePlanRequest req) {
//        ProductionPlan p = findPlanById(id);
//        mapper.updatePlan(req, p);
//        return mapper.toPlanResponse(planRepo.save(p));
//    }
public ProductionDtos.PlanResponse updatePlan(Long id,
                                              ProductionDtos.UpdatePlanRequest req) {
    ProductionPlan p = findPlanById(id);
    ProductionPlan.Status prev = p.getStatus();   // ← capture BEFORE update

    mapper.updatePlan(req, p);
    p = planRepo.save(p);

    // ── Automation on status transition ───────────────────────
    if (req.getStatus() != null && req.getStatus() != prev) {

        if (req.getStatus() == ProductionPlan.Status.IN_PROGRESS) {
            automationService.onPlanSetToInProgress(p);

        } else if (req.getStatus() == ProductionPlan.Status.COMPLETED) {
            automationService.onPlanSetToCompleted(p);
        }
    }

    return mapper.toPlanResponse(p);
}

    public void deletePlan(Long id) { findPlanById(id); planRepo.deleteById(id); }

    // ── Machines ─────────────────────────────────────────────

    public ProductionDtos.MachineResponse createMachine(ProductionDtos.CreateMachineRequest req) {
        Machine m = mapper.toMachine(req);
        if (req.getStatus() != null) m.setStatus(req.getStatus());
        return mapper.toMachineResponse(machineRepo.save(m));
    }

    @Transactional(readOnly = true)
    public List<ProductionDtos.MachineResponse> getAllMachines() {
        return machineRepo.findAll().stream().map(mapper::toMachineResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductionDtos.MachineResponse getMachineById(Long id) {
        return mapper.toMachineResponse(findMachineById(id));
    }

    public ProductionDtos.MachineResponse updateMachine(Long id, ProductionDtos.UpdateMachineRequest req) {
        Machine machine = findMachineById(id);
        Machine.Status prev = machine.getStatus();
        mapper.updateMachine(req, machine);
        machine = machineRepo.save(machine);
        if (req.getStatus() != null && req.getStatus() != prev) {
            if (req.getStatus() == Machine.Status.MAINTENANCE)
                automationService.onMachineSetToMaintenance(machine);
            else if (req.getStatus() == Machine.Status.ACTIVE && prev == Machine.Status.MAINTENANCE)
                automationService.onMachineSetToActive(machine);
        }
        return mapper.toMachineResponse(machine);
    }

    public void deleteMachine(Long id) { findMachineById(id); machineRepo.deleteById(id); }

    // ── Work Orders ───────────────────────────────────────────

    /**
     * Create Work Order.
     * AUTO: Creates a MaterialRequest in Inventory for every raw material item
     *       linked to the plan's plant warehouse, so workOrderId is populated.
     * AUTO: Notifies all roles about the new work order.
     */
    public ProductionDtos.WorkOrderResponse createWorkOrder(ProductionDtos.CreateWorkOrderRequest req) {
        ProductionPlan plan = findPlanById(req.getPlanId());
        WorkOrder wo = WorkOrder.builder()
                .plan(plan)
                .productId(req.getProductId())
                .quantity(req.getQuantity())
                .scheduledStart(req.getScheduledStart())
                .scheduledEnd(req.getScheduledEnd())
                .status(req.getStatus() != null ? req.getStatus() : WorkOrder.Status.PENDING)
                .build();
        if (req.getMachineId() != null) wo.setMachine(findMachineById(req.getMachineId()));
        wo = workOrderRepo.save(wo);

        // AUTO: Create MaterialRequest for this work order with the workOrderId populated
        autoCreateMaterialRequestForWorkOrder(wo);

        // AUTO: Notify all roles
        automationService.notifyAllRoles(
                String.format("🔧 New Work Order WO-%d created | Product: %s | Qty: %d | Plan: '%s' | Machine: %s | Status: %s",
                        wo.getWorkOrderId(), wo.getProductId(), wo.getQuantity(),
                        plan.getPlanName(),
                        wo.getMachine() != null ? wo.getMachine().getName() : "Not assigned",
                        wo.getStatus()),
                com.manutrack.module.notification.entity.Notification.Category.WORK_ORDER
        );

        log.info("[AUTO] WO-{} created → MaterialRequest + notifications sent", wo.getWorkOrderId());
        return mapper.toWorkOrderResponse(wo);
    }

    @Transactional(readOnly = true)
    public List<ProductionDtos.WorkOrderResponse> getAllWorkOrders() {
        return workOrderRepo.findAll().stream().map(mapper::toWorkOrderResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductionDtos.WorkOrderResponse getWorkOrderById(Long id) {
        return mapper.toWorkOrderResponse(findWorkOrderById(id));
    }

    @Transactional(readOnly = true)
    public List<ProductionDtos.WorkOrderResponse> getWorkOrdersByPlan(Long planId) {
        return workOrderRepo.findByPlan_PlanId(planId).stream().map(mapper::toWorkOrderResponse).toList();
    }

    /**
     * Update Work Order status.
     *
     * PENDING → IN_PROGRESS : Machine status → ACTIVE (if assigned)
     * IN_PROGRESS → COMPLETED : Machine status → IDLE | auto-add finished goods to inventory
     * ANY → HALTED : Machine stays as-is, notifications sent
     *
     * AUTO: Notifies all roles on every status change.
     */
    public ProductionDtos.WorkOrderResponse updateWorkOrder(Long id, ProductionDtos.UpdateWorkOrderRequest req) {
        WorkOrder wo = findWorkOrderById(id);
        WorkOrder.Status prev = wo.getStatus();
        mapper.updateWorkOrder(req, wo);

        // Auto-set actual timestamps
        if (req.getStatus() == WorkOrder.Status.IN_PROGRESS && wo.getActualStart() == null)
            wo.setActualStart(LocalDateTime.now());
        if (req.getStatus() == WorkOrder.Status.COMPLETED && wo.getActualEnd() == null)
            wo.setActualEnd(LocalDateTime.now());

        if (req.getMachineId() != null) wo.setMachine(findMachineById(req.getMachineId()));
        wo = workOrderRepo.save(wo);

        // Handle machine status + automation on status change
        if (req.getStatus() != null && req.getStatus() != prev) {

            if (req.getStatus() == WorkOrder.Status.IN_PROGRESS) {
                // Machine assigned → set ACTIVE
                if (wo.getMachine() != null) {
                    Machine machine = wo.getMachine();
                    if (machine.getStatus() == Machine.Status.IDLE) {
                        machine.setStatus(Machine.Status.ACTIVE);
                        machineRepo.save(machine);
                        log.info("[AUTO] Machine '{}' → ACTIVE (WO-{} started)",
                                machine.getName(), wo.getWorkOrderId());
                    }
                }
                automationService.onWorkOrderStarted(wo);
                automationService.notifyAllRoles(
                        String.format("▶️ Work Order WO-%d IN PROGRESS | Product: %s | Machine: %s | Started: %s",
                                wo.getWorkOrderId(), wo.getProductId(),
                                wo.getMachine() != null ? wo.getMachine().getName() : "No machine",
                                wo.getActualStart() != null ? wo.getActualStart().toLocalDate().toString() : "Now"),
                        com.manutrack.module.notification.entity.Notification.Category.WORK_ORDER
                );

            } else if (req.getStatus() == WorkOrder.Status.COMPLETED) {
                // Machine assigned → set back to IDLE
                if (wo.getMachine() != null) {
                    Machine machine = wo.getMachine();
                    machine.setStatus(Machine.Status.IDLE);
                    machineRepo.save(machine);
                    log.info("[AUTO] Machine '{}' → IDLE (WO-{} completed)",
                            machine.getName(), wo.getWorkOrderId());
                }
                automationService.onWorkOrderCompleted(wo);
                automationService.notifyAllRoles(
                        String.format("✅ Work Order WO-%d COMPLETED | Product: %s | Qty: %d units produced | Machine '%s' back to IDLE",
                                wo.getWorkOrderId(), wo.getProductId(), wo.getQuantity(),
                                wo.getMachine() != null ? wo.getMachine().getName() : "N/A"),
                        com.manutrack.module.notification.entity.Notification.Category.WORK_ORDER
                );

            } else if (req.getStatus() == WorkOrder.Status.HALTED) {
                automationService.onWorkOrderHalted(wo);
                automationService.notifyAllRoles(
                        String.format("🚨 Work Order WO-%d HALTED | Product: %s | Machine: %s | Plan: '%s' — Requires immediate attention.",
                                wo.getWorkOrderId(), wo.getProductId(),
                                wo.getMachine() != null ? wo.getMachine().getName() : "No machine",
                                wo.getPlan() != null ? wo.getPlan().getPlanName() : "N/A"),
                        com.manutrack.module.notification.entity.Notification.Category.WORK_ORDER
                );

            } else if (req.getStatus() == WorkOrder.Status.PENDING) {
                automationService.notifyAllRoles(
                        String.format("🔄 Work Order WO-%d reset to PENDING | Product: %s | Ready to restart.",
                                wo.getWorkOrderId(), wo.getProductId()),
                        com.manutrack.module.notification.entity.Notification.Category.WORK_ORDER
                );
            }
        }
        return mapper.toWorkOrderResponse(wo);
    }

    public void deleteWorkOrder(Long id) { findWorkOrderById(id); workOrderRepo.deleteById(id); }

    // ── Private Helpers ───────────────────────────────────────

    /**
     * When a Work Order is created, auto-create a MaterialRequest in Inventory
     * with the workOrderId populated so it shows up in the Material Requests table.
     * Creates one request per raw material item available in the system.
     */
    private void autoCreateMaterialRequestForWorkOrder(WorkOrder wo) {
        // Find all RAW_MATERIAL items — create a request for each one needed
        List<InventoryItem> rawMaterials = inventoryItemRepo.findAll().stream()
                .filter(item -> item.getItemType() == InventoryItem.ItemType.RAW_MATERIAL)
                .toList();

        // Only create if there are raw materials and no existing PENDING request for this WO
        boolean alreadyExists = materialRequestRepo.findByWorkOrderId(wo.getWorkOrderId())
                .stream().anyMatch(r -> r.getStatus() == MaterialRequest.Status.PENDING);

        if (!alreadyExists && !rawMaterials.isEmpty()) {
            // Create one consolidated material request for the primary raw material
            InventoryItem primaryItem = rawMaterials.get(0);
            double requestedQty = wo.getQuantity() * 0.5; // Estimate 0.5 units of material per unit produced

            MaterialRequest mr = MaterialRequest.builder()
                    .workOrderId(wo.getWorkOrderId())       // ← workOrderId properly set
                    .item(primaryItem)
                    .quantity(Math.max(requestedQty, 1.0))
                    .requestedDate(LocalDate.now())
                    .status(MaterialRequest.Status.PENDING)
                    .build();
            materialRequestRepo.save(mr);

            log.info("[AUTO] MaterialRequest created for WO-{}: {} x {} {}",
                    wo.getWorkOrderId(), mr.getQuantity(),
                    primaryItem.getUnitOfMeasure(), primaryItem.getDescription());
        }
    }

    private ProductionPlan findPlanById(Long id) {
        return planRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("ProductionPlan", "id", id));
    }

    private Machine findMachineById(Long id) {
        return machineRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Machine", "id", id));
    }

    private WorkOrder findWorkOrderById(Long id) {
        return workOrderRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("WorkOrder", "id", id));
    }
}
