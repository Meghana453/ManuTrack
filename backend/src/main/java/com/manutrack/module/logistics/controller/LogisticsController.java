package com.manutrack.module.logistics.controller;

import com.manutrack.module.logistics.dto.LogisticsDtos;
import com.manutrack.module.logistics.entity.Shipment;
import com.manutrack.module.logistics.service.LogisticsService;
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
@RequestMapping("/logistics")

@RequiredArgsConstructor @Tag(name = "Logistics & Shipment Management")
public class LogisticsController {

    private final LogisticsService logisticsService;

    // Carriers
    @PostMapping("/carriers") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Create carrier")
    public ResponseEntity<LogisticsDtos.CarrierResponse> createCarrier(@Valid @RequestBody LogisticsDtos.CreateCarrierRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logisticsService.createCarrier(req));
    }
    @GetMapping("/carriers") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get all carriers")
    public ResponseEntity<List<LogisticsDtos.CarrierResponse>> getAllCarriers() {
        return ResponseEntity.ok(logisticsService.getAllCarriers());
    }
    @GetMapping("/carriers/{id}") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get carrier by ID")
    public ResponseEntity<LogisticsDtos.CarrierResponse> getCarrierById(@PathVariable Long id) {
        return ResponseEntity.ok(logisticsService.getCarrierById(id));
    }
    @PutMapping("/carriers/{id}") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Update carrier")
    public ResponseEntity<LogisticsDtos.CarrierResponse> updateCarrier(@PathVariable Long id, @Valid @RequestBody LogisticsDtos.UpdateCarrierRequest req) {
        return ResponseEntity.ok(logisticsService.updateCarrier(id, req));
    }
    @DeleteMapping("/carriers/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete carrier")
    public ResponseEntity<Void> deleteCarrier(@PathVariable Long id) {
        logisticsService.deleteCarrier(id); return ResponseEntity.noContent().build();
    }

    // Routes
    @PostMapping("/routes") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Create route")
    public ResponseEntity<LogisticsDtos.RouteResponse> createRoute(@Valid @RequestBody LogisticsDtos.CreateRouteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logisticsService.createRoute(req));
    }
    @GetMapping("/routes") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get all routes")
    public ResponseEntity<List<LogisticsDtos.RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(logisticsService.getAllRoutes());
    }
    @GetMapping("/routes/{id}") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get route by ID")
    public ResponseEntity<LogisticsDtos.RouteResponse> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(logisticsService.getRouteById(id));
    }
    @PutMapping("/routes/{id}") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Update route")
    public ResponseEntity<LogisticsDtos.RouteResponse> updateRoute(@PathVariable Long id, @Valid @RequestBody LogisticsDtos.UpdateRouteRequest req) {
        return ResponseEntity.ok(logisticsService.updateRoute(id, req));
    }
    @DeleteMapping("/routes/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete route")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        logisticsService.deleteRoute(id); return ResponseEntity.noContent().build();
    }

    // Shipments
    @PostMapping("/shipments") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Create shipment")
    public ResponseEntity<LogisticsDtos.ShipmentResponse> createShipment(@Valid @RequestBody LogisticsDtos.CreateShipmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logisticsService.createShipment(req));
    }
    @GetMapping("/shipments") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get all shipments")
    public ResponseEntity<List<LogisticsDtos.ShipmentResponse>> getAllShipments() {
        return ResponseEntity.ok(logisticsService.getAllShipments());
    }
    @GetMapping("/shipments/{id}") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get shipment by ID")
    public ResponseEntity<LogisticsDtos.ShipmentResponse> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(logisticsService.getShipmentById(id));
    }
    @GetMapping("/shipments/status/{status}") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Get shipments by status")
    public ResponseEntity<List<LogisticsDtos.ShipmentResponse>> getShipmentsByStatus(@PathVariable Shipment.Status status) {
        return ResponseEntity.ok(logisticsService.getShipmentsByStatus(status));
    }
    @PutMapping("/shipments/{id}") @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
    @Operation(summary = "Update shipment")
    public ResponseEntity<LogisticsDtos.ShipmentResponse> updateShipment(@PathVariable Long id, @Valid @RequestBody LogisticsDtos.UpdateShipmentRequest req) {
        return ResponseEntity.ok(logisticsService.updateShipment(id, req));
    }
    @DeleteMapping("/shipments/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete shipment")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        logisticsService.deleteShipment(id); return ResponseEntity.noContent().build();
    }
}
