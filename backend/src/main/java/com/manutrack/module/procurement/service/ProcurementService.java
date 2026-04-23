//package com.manutrack.module.procurement.service;
//
//import com.manutrack.automation.AutomationService;
//import com.manutrack.exception.ResourceNotFoundException;
//import com.manutrack.module.procurement.dto.ProcurementDtos;
//import com.manutrack.module.procurement.entity.Invoice;
//import com.manutrack.module.procurement.entity.PurchaseOrder;
//import com.manutrack.module.procurement.entity.Vendor;
//import com.manutrack.module.procurement.mapper.ProcurementMapper;
//import com.manutrack.module.procurement.repository.InvoiceRepository;
//import com.manutrack.module.procurement.repository.PurchaseOrderRepository;
//import com.manutrack.module.procurement.repository.VendorRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDate;
//import java.util.List;
//
//@Service @RequiredArgsConstructor @Transactional @Slf4j
//public class ProcurementService {
//
//    private final VendorRepository vendorRepo;
//    private final PurchaseOrderRepository poRepo;
//    private final InvoiceRepository invoiceRepo;
//    private final ProcurementMapper mapper;
//    private final AutomationService automationService;
//
//    // ── Vendors ───────────────────────────────────────────
//
//    public ProcurementDtos.VendorResponse createVendor(ProcurementDtos.CreateVendorRequest req) {
//        Vendor v = Vendor.builder().name(req.getName()).contactInfo(req.getContactInfo())
//                .rating(req.getRating() != null ? req.getRating() : 0.0)
//                .status(req.getStatus() != null ? req.getStatus() : Vendor.Status.ACTIVE).build();
//        return mapper.toVendorResponse(vendorRepo.save(v));
//    }
//    @Transactional(readOnly=true) public List<ProcurementDtos.VendorResponse> getAllVendors() { return vendorRepo.findAll().stream().map(mapper::toVendorResponse).toList(); }
//    @Transactional(readOnly=true) public ProcurementDtos.VendorResponse getVendorById(Long id) { return mapper.toVendorResponse(findVendorById(id)); }
//    public ProcurementDtos.VendorResponse updateVendor(Long id, ProcurementDtos.UpdateVendorRequest req) {
//        Vendor v = findVendorById(id);
//        mapper.updateVendor(req, v);
//        v = vendorRepo.save(v);
//        if (req.getRating() != null && req.getRating() < 3.0 && req.getRating() > 0) {
//            automationService.onVendorRatingLow(v);
//        }
//        return mapper.toVendorResponse(v);
//    }
//    public void deleteVendor(Long id) { findVendorById(id); vendorRepo.deleteById(id); }
//
//    // ── Purchase Orders ───────────────────────────────────
//
//    public ProcurementDtos.POResponse createPO(ProcurementDtos.CreatePORequest req) {
//        Vendor vendor = findVendorById(req.getVendorId());
//        PurchaseOrder po = PurchaseOrder.builder()
//                .vendor(vendor).itemId(req.getItemId()).quantity(req.getQuantity()).unitPrice(req.getUnitPrice())
//                .orderDate(req.getOrderDate() != null ? req.getOrderDate() : LocalDate.now())
//                .expectedDeliveryDate(req.getExpectedDeliveryDate())
//                .status(PurchaseOrder.Status.OPEN).build();
//        return mapper.toPOResponse(poRepo.save(po));
//    }
//    @Transactional(readOnly=true) public List<ProcurementDtos.POResponse> getAllPOs() { return poRepo.findAll().stream().map(mapper::toPOResponse).toList(); }
//    @Transactional(readOnly=true) public ProcurementDtos.POResponse getPOById(Long id) { return mapper.toPOResponse(findPOById(id)); }
//
//    public ProcurementDtos.POResponse updatePO(Long id, ProcurementDtos.UpdatePORequest req) {
//        PurchaseOrder po = findPOById(id);
//        PurchaseOrder.Status prev = po.getStatus();
//        mapper.updatePO(req, po);
//        po = poRepo.save(po);
//
//        // ── AUTOMATION: PO delivered → auto-update inventory stock + auto-create invoice ──
//        if (req.getStatus() != null && req.getStatus() == PurchaseOrder.Status.DELIVERED
//                && prev != PurchaseOrder.Status.DELIVERED) {
//            automationService.onPurchaseOrderDelivered(po);
//        }
//        return mapper.toPOResponse(po);
//    }
//    public void deletePO(Long id) { findPOById(id); poRepo.deleteById(id); }
//
//    // ── Invoices ──────────────────────────────────────────
//
//    public ProcurementDtos.InvoiceResponse createInvoice(ProcurementDtos.CreateInvoiceRequest req) {
//        PurchaseOrder po = findPOById(req.getPoId());
//        Invoice inv = Invoice.builder().purchaseOrder(po).amount(req.getAmount())
//                .issueDate(req.getIssueDate() != null ? req.getIssueDate() : LocalDate.now())
//                .dueDate(req.getDueDate()).status(Invoice.Status.PENDING).build();
//        return mapper.toInvoiceResponse(invoiceRepo.save(inv));
//    }
//    @Transactional(readOnly=true) public List<ProcurementDtos.InvoiceResponse> getAllInvoices() { return invoiceRepo.findAll().stream().map(mapper::toInvoiceResponse).toList(); }
//    @Transactional(readOnly=true) public ProcurementDtos.InvoiceResponse getInvoiceById(Long id) { return mapper.toInvoiceResponse(findInvoiceById(id)); }
//
//    public ProcurementDtos.InvoiceResponse updateInvoice(Long id, ProcurementDtos.UpdateInvoiceRequest req) {
//        Invoice inv = findInvoiceById(id);
//        Invoice.Status prev = inv.getStatus();
//        mapper.updateInvoice(req, inv);
//        inv = invoiceRepo.save(inv);
//
//        // ── AUTOMATION: Invoice goes overdue → notify procurement officer ──
//        if (req.getStatus() != null && req.getStatus() == Invoice.Status.OVERDUE
//                && prev != Invoice.Status.OVERDUE) {
//            automationService.onInvoiceOverdue(inv);
//        }
//        return mapper.toInvoiceResponse(inv);
//    }
//    public void deleteInvoice(Long id) { findInvoiceById(id); invoiceRepo.deleteById(id); }
//
//    private Vendor findVendorById(Long id) { return vendorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendor","id",id)); }
//    private PurchaseOrder findPOById(Long id) { return poRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder","id",id)); }
//    private Invoice findInvoiceById(Long id) { return invoiceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice","id",id)); }
//}
package com.manutrack.module.procurement.service;

import com.manutrack.automation.AutomationService;
import com.manutrack.exception.ResourceNotFoundException;
import com.manutrack.module.procurement.dto.ProcurementDtos;
import com.manutrack.module.procurement.entity.Invoice;
import com.manutrack.module.procurement.entity.PurchaseOrder;
import com.manutrack.module.procurement.entity.Vendor;
import com.manutrack.module.procurement.mapper.ProcurementMapper;
import com.manutrack.module.procurement.repository.InvoiceRepository;
import com.manutrack.module.procurement.repository.PurchaseOrderRepository;
import com.manutrack.module.procurement.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class ProcurementService {

    private final VendorRepository vendorRepo;
    private final PurchaseOrderRepository poRepo;
    private final InvoiceRepository invoiceRepo;
    private final ProcurementMapper mapper;
    private final AutomationService automationService;

    // ── Vendors ───────────────────────────────────────────

    public ProcurementDtos.VendorResponse createVendor(ProcurementDtos.CreateVendorRequest req) {
        Vendor v = Vendor.builder().name(req.getName()).contactInfo(req.getContactInfo())
                .rating(req.getRating() != null ? req.getRating() : 0.0)
                .status(req.getStatus() != null ? req.getStatus() : Vendor.Status.ACTIVE).build();
        Vendor saved = vendorRepo.save(v);
        automationService.notifyAllRoles(
                String.format("🏢 New Vendor '%s' added | Contact: %s | Rating: %.1f | Status: %s",
                        saved.getName(), saved.getContactInfo() != null ? saved.getContactInfo() : "N/A",
                        saved.getRating() != null ? saved.getRating() : 0.0, saved.getStatus()),
                com.manutrack.module.notification.entity.Notification.Category.PROCUREMENT);
        return mapper.toVendorResponse(saved);
    }
    @Transactional(readOnly=true) public List<ProcurementDtos.VendorResponse> getAllVendors() { return vendorRepo.findAll().stream().map(mapper::toVendorResponse).toList(); }
    @Transactional(readOnly=true) public ProcurementDtos.VendorResponse getVendorById(Long id) { return mapper.toVendorResponse(findVendorById(id)); }
    public ProcurementDtos.VendorResponse updateVendor(Long id, ProcurementDtos.UpdateVendorRequest req) {
        Vendor v = findVendorById(id);
        mapper.updateVendor(req, v);
        v = vendorRepo.save(v);
        if (req.getRating() != null && req.getRating() < 3.0 && req.getRating() > 0) {
            automationService.onVendorRatingLow(v);
        }
        return mapper.toVendorResponse(v);
    }
    public void deleteVendor(Long id) { findVendorById(id); vendorRepo.deleteById(id); }

    // ── Purchase Orders ───────────────────────────────────

//    public ProcurementDtos.POResponse createPO(ProcurementDtos.CreatePORequest req) {
//        Vendor vendor = findVendorById(req.getVendorId());
//        PurchaseOrder po = PurchaseOrder.builder()
//                .vendor(vendor).itemId(req.getItemId()).quantity(req.getQuantity()).unitPrice(req.getUnitPrice())
//                .orderDate(req.getOrderDate() != null ? req.getOrderDate() : LocalDate.now())
//                .expectedDeliveryDate(req.getExpectedDeliveryDate())
//                .status(PurchaseOrder.Status.OPEN).build();
//        PurchaseOrder saved = poRepo.save(po);
//        automationService.notifyAllRoles(
//                String.format("🛒 New Purchase Order #%d created | Vendor: %s | Item ID: %d | Qty: %.1f | Value: $%.2f | Expected: %s",
//                        saved.getPoId(), saved.getVendor().getName(), saved.getItemId(),
//                        saved.getQuantity(), saved.getQuantity() * saved.getUnitPrice(),
//                        saved.getExpectedDeliveryDate() != null ? saved.getExpectedDeliveryDate().toString() : "N/A"),
//                com.manutrack.module.notification.entity.Notification.Category.PROCUREMENT);
//        return mapper.toPOResponse(saved);
//    }
public ProcurementDtos.POResponse createPO(ProcurementDtos.CreatePORequest req) {
    Vendor vendor = findVendorById(req.getVendorId());

    PurchaseOrder po = PurchaseOrder.builder()
            .vendor(vendor)
            .itemId(req.getItemId())
            .quantity(req.getQuantity())
            .unitPrice(req.getUnitPrice())
            .orderDate(req.getOrderDate() != null ? req.getOrderDate() : LocalDate.now())
            .expectedDeliveryDate(req.getExpectedDeliveryDate())
            .status(PurchaseOrder.Status.OPEN)
            .build();

    PurchaseOrder savedPO = poRepo.save(po);

    // ✅ AUTOMATION: Auto-create invoice when PO is created
    Invoice invoice = Invoice.builder()
            .purchaseOrder(savedPO)
            .amount(savedPO.getQuantity() * savedPO.getUnitPrice())
            .issueDate(LocalDate.now())
            .status(Invoice.Status.PENDING)
            .build();

    invoiceRepo.save(invoice);

    return mapper.toPOResponse(savedPO);
}
    @Transactional(readOnly=true) public List<ProcurementDtos.POResponse> getAllPOs() { return poRepo.findAll().stream().map(mapper::toPOResponse).toList(); }
    @Transactional(readOnly=true) public ProcurementDtos.POResponse getPOById(Long id) { return mapper.toPOResponse(findPOById(id)); }

    public ProcurementDtos.POResponse updatePO(Long id, ProcurementDtos.UpdatePORequest req) {
        PurchaseOrder po = findPOById(id);
        PurchaseOrder.Status prev = po.getStatus();
        mapper.updatePO(req, po);
        po = poRepo.save(po);

        // ── AUTOMATION: PO delivered → auto-update inventory stock + auto-create invoice ──
        if (req.getStatus() != null && req.getStatus() == PurchaseOrder.Status.DELIVERED
                && prev != PurchaseOrder.Status.DELIVERED) {
            automationService.onPurchaseOrderDelivered(po);
        }
        return mapper.toPOResponse(po);
    }
    public void deletePO(Long id) { findPOById(id); poRepo.deleteById(id); }

    // ── Invoices ──────────────────────────────────────────

    public ProcurementDtos.InvoiceResponse createInvoice(ProcurementDtos.CreateInvoiceRequest req) {
        PurchaseOrder po = findPOById(req.getPoId());
        Invoice inv = Invoice.builder().purchaseOrder(po).amount(req.getAmount())
                .issueDate(req.getIssueDate() != null ? req.getIssueDate() : LocalDate.now())
                .dueDate(req.getDueDate()).status(Invoice.Status.PENDING).build();
        Invoice saved = invoiceRepo.save(inv);
        automationService.notifyAllRoles(
                String.format("🧾 New Invoice #%d created | PO #%d | Vendor: %s | Amount: $%.2f | Due: %s",
                        saved.getInvoiceId(), saved.getPurchaseOrder().getPoId(),
                        saved.getPurchaseOrder().getVendor().getName(),
                        saved.getAmount(),
                        saved.getDueDate() != null ? saved.getDueDate().toString() : "N/A"),
                com.manutrack.module.notification.entity.Notification.Category.PROCUREMENT);
        return mapper.toInvoiceResponse(saved);
    }
    @Transactional(readOnly=true) public List<ProcurementDtos.InvoiceResponse> getAllInvoices() { return invoiceRepo.findAll().stream().map(mapper::toInvoiceResponse).toList(); }
    @Transactional(readOnly=true) public ProcurementDtos.InvoiceResponse getInvoiceById(Long id) { return mapper.toInvoiceResponse(findInvoiceById(id)); }

    public ProcurementDtos.InvoiceResponse updateInvoice(Long id, ProcurementDtos.UpdateInvoiceRequest req) {
        Invoice inv = findInvoiceById(id);
        Invoice.Status prev = inv.getStatus();
        mapper.updateInvoice(req, inv);
        inv = invoiceRepo.save(inv);

        // ── AUTOMATION: Invoice goes overdue → notify procurement officer ──
        if (req.getStatus() != null && req.getStatus() == Invoice.Status.OVERDUE
                && prev != Invoice.Status.OVERDUE) {
            automationService.onInvoiceOverdue(inv);
        }
        return mapper.toInvoiceResponse(inv);
    }
    public void deleteInvoice(Long id) { findInvoiceById(id); invoiceRepo.deleteById(id); }

    private Vendor findVendorById(Long id) { return vendorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendor","id",id)); }
    private PurchaseOrder findPOById(Long id) { return poRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder","id",id)); }
    private Invoice findInvoiceById(Long id) { return invoiceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice","id",id)); }
}
