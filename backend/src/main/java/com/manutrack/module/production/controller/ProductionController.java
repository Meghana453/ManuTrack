package com.manutrack.module.production.controller;

import com.manutrack.module.production.dto.ProductionDtos;
import com.manutrack.module.production.service.ProductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/production")
@RequiredArgsConstructor
@Tag(name = "Production Planning & Scheduling")
public class ProductionController {

    private final ProductionService productionService;

    // ---- Plans ----
    @PostMapping("/plans")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER')")
    @Operation(summary = "Create production plan")
    public ResponseEntity<ProductionDtos.PlanResponse> createPlan(@Valid @RequestBody ProductionDtos.CreatePlanRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionService.createPlan(req));
    }

    @GetMapping("/plans")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get all production plans")
    public ResponseEntity<List<ProductionDtos.PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(productionService.getAllPlans());
    }

    @GetMapping("/plans/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get production plan by ID")
    public ResponseEntity<ProductionDtos.PlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(productionService.getPlanById(id));
    }

    @PutMapping("/plans/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER')")
    @Operation(summary = "Update production plan")
    public ResponseEntity<ProductionDtos.PlanResponse> updatePlan(@PathVariable Long id,
                                                                   @Valid @RequestBody ProductionDtos.UpdatePlanRequest req) {
        return ResponseEntity.ok(productionService.updatePlan(id, req));
    }

    @DeleteMapping("/plans/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER')")
    @Operation(summary = "Delete production plan")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        productionService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Machines ----
    @PostMapping("/machines")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    @Operation(summary = "Create machine")
    public ResponseEntity<ProductionDtos.MachineResponse> createMachine(@Valid @RequestBody ProductionDtos.CreateMachineRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionService.createMachine(req));
    }

    @GetMapping("/machines")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get all machines")
    public ResponseEntity<List<ProductionDtos.MachineResponse>> getAllMachines() {
        return ResponseEntity.ok(productionService.getAllMachines());
    }

    @GetMapping("/machines/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get machine by ID")
    public ResponseEntity<ProductionDtos.MachineResponse> getMachineById(@PathVariable Long id) {
        return ResponseEntity.ok(productionService.getMachineById(id));
    }

    @PutMapping("/machines/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    @Operation(summary = "Update machine")
    public ResponseEntity<ProductionDtos.MachineResponse> updateMachine(@PathVariable Long id,
                                                                         @Valid @RequestBody ProductionDtos.UpdateMachineRequest req) {
        return ResponseEntity.ok(productionService.updateMachine(id, req));
    }

    @DeleteMapping("/machines/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete machine")
    public ResponseEntity<Void> deleteMachine(@PathVariable Long id) {
        productionService.deleteMachine(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Work Orders ----
    @PostMapping("/work-orders")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Create work order")
    public ResponseEntity<ProductionDtos.WorkOrderResponse> createWorkOrder(@Valid @RequestBody ProductionDtos.CreateWorkOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionService.createWorkOrder(req));
    }

    @GetMapping("/work-orders")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get all work orders")
    public ResponseEntity<List<ProductionDtos.WorkOrderResponse>> getAllWorkOrders() {
        return ResponseEntity.ok(productionService.getAllWorkOrders());
    }

    @GetMapping("/work-orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get work order by ID")
    public ResponseEntity<ProductionDtos.WorkOrderResponse> getWorkOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(productionService.getWorkOrderById(id));
    }

    @GetMapping("/plans/{planId}/work-orders")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Get work orders by plan")
    public ResponseEntity<List<ProductionDtos.WorkOrderResponse>> getWorkOrdersByPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(productionService.getWorkOrdersByPlan(planId));
    }

    @PutMapping("/work-orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER','SUPERVISOR')")
    @Operation(summary = "Update work order")
    public ResponseEntity<ProductionDtos.WorkOrderResponse> updateWorkOrder(@PathVariable Long id,
                                                                             @Valid @RequestBody ProductionDtos.UpdateWorkOrderRequest req) {
        return ResponseEntity.ok(productionService.updateWorkOrder(id, req));
    }

    @DeleteMapping("/work-orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PLANNER')")
    @Operation(summary = "Delete work order")
    public ResponseEntity<Void> deleteWorkOrder(@PathVariable Long id) {
        productionService.deleteWorkOrder(id);
        return ResponseEntity.noContent().build();
    }
}
