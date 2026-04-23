package com.manutrack.module.procurement.controller;

import com.manutrack.module.procurement.dto.ProcurementDtos;
import com.manutrack.module.procurement.service.ProcurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/procurement")
@RequiredArgsConstructor @Tag(name = "Procurement & Vendor Management")
public class ProcurementController {

    private final ProcurementService procurementService;

    // Vendors
    @PostMapping("/vendors") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Create vendor")
    public ResponseEntity<ProcurementDtos.VendorResponse> createVendor(@Valid @RequestBody ProcurementDtos.CreateVendorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procurementService.createVendor(req));
    }
    @GetMapping("/vendors") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT','PLANNER')")
    @Operation(summary = "Get all vendors")
    public ResponseEntity<List<ProcurementDtos.VendorResponse>> getAllVendors() {
        return ResponseEntity.ok(procurementService.getAllVendors());
    }
    @GetMapping("/vendors/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Get vendor by ID")
    public ResponseEntity<ProcurementDtos.VendorResponse> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.getVendorById(id));
    }
    @PutMapping("/vendors/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Update vendor")
    public ResponseEntity<ProcurementDtos.VendorResponse> updateVendor(@PathVariable Long id, @Valid @RequestBody ProcurementDtos.UpdateVendorRequest req) {
        return ResponseEntity.ok(procurementService.updateVendor(id, req));
    }
    @DeleteMapping("/vendors/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete vendor")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        procurementService.deleteVendor(id); return ResponseEntity.noContent().build();
    }

    // Purchase Orders
    @PostMapping("/purchase-orders") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Create purchase order")
    public ResponseEntity<ProcurementDtos.POResponse> createPO(@Valid @RequestBody ProcurementDtos.CreatePORequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procurementService.createPO(req));
    }
    @GetMapping("/purchase-orders") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT','PLANNER')")
    @Operation(summary = "Get all purchase orders")
    public ResponseEntity<List<ProcurementDtos.POResponse>> getAllPOs() {
        return ResponseEntity.ok(procurementService.getAllPOs());
    }
    @GetMapping("/purchase-orders/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Get purchase order by ID")
    public ResponseEntity<ProcurementDtos.POResponse> getPOById(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.getPOById(id));
    }
    @PutMapping("/purchase-orders/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Update purchase order")
    public ResponseEntity<ProcurementDtos.POResponse> updatePO(@PathVariable Long id, @Valid @RequestBody ProcurementDtos.UpdatePORequest req) {
        return ResponseEntity.ok(procurementService.updatePO(id, req));
    }
    @DeleteMapping("/purchase-orders/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete purchase order")
    public ResponseEntity<Void> deletePO(@PathVariable Long id) {
        procurementService.deletePO(id); return ResponseEntity.noContent().build();
    }

    // Invoices
    @PostMapping("/invoices") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Create invoice")
    public ResponseEntity<ProcurementDtos.InvoiceResponse> createInvoice(@Valid @RequestBody ProcurementDtos.CreateInvoiceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procurementService.createInvoice(req));
    }
    @GetMapping("/invoices") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Get all invoices")
    public ResponseEntity<List<ProcurementDtos.InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(procurementService.getAllInvoices());
    }
    @GetMapping("/invoices/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ProcurementDtos.InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.getInvoiceById(id));
    }
    @PutMapping("/invoices/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT')")
    @Operation(summary = "Update invoice")
    public ResponseEntity<ProcurementDtos.InvoiceResponse> updateInvoice(@PathVariable Long id, @Valid @RequestBody ProcurementDtos.UpdateInvoiceRequest req) {
        return ResponseEntity.ok(procurementService.updateInvoice(id, req));
    }
    @DeleteMapping("/invoices/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete invoice")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        procurementService.deleteInvoice(id); return ResponseEntity.noContent().build();
    }
}
