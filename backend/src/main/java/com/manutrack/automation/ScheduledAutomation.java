package com.manutrack.automation;

import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.repository.InventoryItemRepository;
import com.manutrack.module.logistics.entity.Shipment;
import com.manutrack.module.logistics.repository.ShipmentRepository;
import com.manutrack.module.procurement.entity.Invoice;
import com.manutrack.module.procurement.entity.Vendor;
import com.manutrack.module.procurement.repository.InvoiceRepository;
import com.manutrack.module.procurement.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledAutomation {

    private final InvoiceRepository       invoiceRepo;
    private final InventoryItemRepository  inventoryItemRepo;
    private final ShipmentRepository       shipmentRepo;
    private final VendorRepository         vendorRepo;
    private final AutomationService        automationService;



    @Scheduled(fixedDelay = 7_200_000, initialDelay = 120_000)
    @Transactional
    public void checkPersistentStockIssues() {
        List<InventoryItem> outOfStock = inventoryItemRepo
                .findByStatus(InventoryItem.Status.OUT_OF_STOCK);

        if (!outOfStock.isEmpty()) {
            log.info("[SCHEDULER] {} item(s) still OUT_OF_STOCK — re-triggering emergency PO check",
                    outOfStock.size());
            outOfStock.forEach(item -> automationService.onStockChanged(item));
        }
    }


    @Scheduled(fixedDelay = 3_600_000, initialDelay = 60_000)
    @Transactional
    public void checkOverdueInvoices() {
        List<Invoice> overdueNow = invoiceRepo.findAll().stream()
                .filter(inv -> inv.getStatus() == Invoice.Status.PENDING
                        && inv.getDueDate() != null
                        && inv.getDueDate().isBefore(LocalDate.now()))
                .toList();

        if (!overdueNow.isEmpty()) {
            log.info("[SCHEDULER] Found {} overdue invoice(s) — auto-marking OVERDUE", overdueNow.size());
        }

        overdueNow.forEach(inv -> {
            inv.setStatus(Invoice.Status.OVERDUE);
            invoiceRepo.save(inv);
            automationService.onInvoiceOverdue(inv);
            log.info("[SCHEDULER] Invoice #{} auto-marked OVERDUE (was due {})",
                    inv.getInvoiceId(), inv.getDueDate());
        });
    }



    // JOB 2 — Delayed Shipment Detection
    // Runs every hour. Finds SCHEDULED shipments whose scheduledDate has passed → marks DELAYED + notifies.
    @Scheduled(fixedDelay = 3_600_000, initialDelay = 90_000)
    @Transactional
    public void checkDelayedShipments() {
        List<Shipment> delayedNow = shipmentRepo.findByStatus(Shipment.Status.SCHEDULED)
                .stream()
                .filter(s -> s.getScheduledDate() != null
                        && s.getScheduledDate().isBefore(LocalDate.now()))
                .toList();

        if (!delayedNow.isEmpty()) {
            log.info("[SCHEDULER] Found {} shipment(s) past scheduled date — auto-marking DELAYED",
                    delayedNow.size());
        }

        delayedNow.forEach(s -> {
            s.setStatus(Shipment.Status.DELAYED);
            shipmentRepo.save(s);
            automationService.onShipmentDelayed(s);
            log.info("[SCHEDULER] Shipment #{} to '{}' auto-marked DELAYED (was scheduled {})",
                    s.getShipmentId(), s.getDestination(), s.getScheduledDate());
        });
    }


    // JOB 3 — Persistent Low/Out-of-Stock Re-check
    // Runs every 2 hours. Re-triggers emergency PO for items still OUT_OF_STOCK.



    // JOB 4 — Vendor Rating Health Check
    // Runs every 6 hours. Flags vendors with rating < 3.0 for review.
    @Scheduled(fixedDelay = 21_600_000, initialDelay = 180_000)
    @Transactional
    public void checkVendorRatings() {
        List<Vendor> poorVendors = vendorRepo.findByStatus(Vendor.Status.ACTIVE)
                .stream()
                .filter(v -> v.getRating() != null && v.getRating() < 3.0 && v.getRating() > 0)
                .toList();

        if (!poorVendors.isEmpty()) {
            log.info("[SCHEDULER] {} vendor(s) with rating < 3.0 flagged for review", poorVendors.size());
            poorVendors.forEach(v -> automationService.onVendorRatingLow(v));
        }
    }
}
