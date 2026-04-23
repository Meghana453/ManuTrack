package com.manutrack.module.inventory.controller;

import com.manutrack.module.inventory.dto.InventoryDtos;
import com.manutrack.module.inventory.service.InventoryService;
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
@RequestMapping("/inventory")
@RequiredArgsConstructor

public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")

    public ResponseEntity<InventoryDtos.ItemResponse> createItem(@Valid @RequestBody InventoryDtos.CreateItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createItem(req));
    }

    @GetMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY','PLANNER','SUPERVISOR')")

    public ResponseEntity<List<InventoryDtos.ItemResponse>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY','PLANNER','SUPERVISOR')")

    public ResponseEntity<InventoryDtos.ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getItemById(id));
    }

    @GetMapping("/items/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")

    public ResponseEntity<List<InventoryDtos.ItemResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @GetMapping("/items/out-of-stock")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")

    public ResponseEntity<List<InventoryDtos.ItemResponse>> getOutOfStockItems() {
        return ResponseEntity.ok(inventoryService.getOutOfStockItems());
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")

    public ResponseEntity<InventoryDtos.ItemResponse> updateItem(@PathVariable Long id,
                                                                  @Valid @RequestBody InventoryDtos.UpdateItemRequest req) {
        return ResponseEntity.ok(inventoryService.updateItem(id, req));
    }

    @PatchMapping("/items/{id}/adjust-stock")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")

    public ResponseEntity<InventoryDtos.ItemResponse> adjustStock(@PathVariable Long id,
                                                                   @Valid @RequestBody InventoryDtos.AdjustStockRequest req) {
        return ResponseEntity.ok(inventoryService.adjustStock(id, req));
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }





    // ---- Material Requests ----
    @PostMapping("/material-requests")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY','SUPERVISOR')")

    public ResponseEntity<InventoryDtos.MaterialRequestResponse> createMaterialRequest(
            @Valid @RequestBody InventoryDtos.CreateMaterialRequestDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createMaterialRequest(req));
    }

    @GetMapping("/material-requests")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY','SUPERVISOR')")

    public ResponseEntity<List<InventoryDtos.MaterialRequestResponse>> getAllMaterialRequests() {
        return ResponseEntity.ok(inventoryService.getAllMaterialRequests());
    }

    @GetMapping("/material-requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY','SUPERVISOR')")

    public ResponseEntity<InventoryDtos.MaterialRequestResponse> getMaterialRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getMaterialRequestById(id));
    }

    @PutMapping("/material-requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")

    public ResponseEntity<InventoryDtos.MaterialRequestResponse> updateMaterialRequest(
            @PathVariable Long id, @Valid @RequestBody InventoryDtos.UpdateMaterialRequestDto req) {
        return ResponseEntity.ok(inventoryService.updateMaterialRequest(id, req));
    }

    @DeleteMapping("/material-requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY')")
//    @Operation(summary = "Delete material request")
    public ResponseEntity<Void> deleteMaterialRequest(@PathVariable Long id) {
        inventoryService.deleteMaterialRequest(id);
        return ResponseEntity.noContent().build();
    }
}
