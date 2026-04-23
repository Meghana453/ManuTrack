package com.manutrack.automation;

import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.entity.MaterialRequest;
import com.manutrack.module.inventory.repository.InventoryItemRepository;
import com.manutrack.module.inventory.repository.MaterialRequestRepository;
import com.manutrack.module.logistics.entity.Shipment;
import com.manutrack.module.logistics.repository.ShipmentRepository;
import com.manutrack.module.notification.entity.Notification;
import com.manutrack.module.notification.repository.NotificationRepository;
import com.manutrack.module.notification.service.NotificationService;
import com.manutrack.module.procurement.entity.Invoice;
import com.manutrack.module.procurement.entity.PurchaseOrder;
import com.manutrack.module.procurement.entity.Vendor;
import com.manutrack.module.procurement.repository.InvoiceRepository;
import com.manutrack.module.procurement.repository.PurchaseOrderRepository;
import com.manutrack.module.production.entity.Machine;
import com.manutrack.module.production.entity.ProductionPlan;
import com.manutrack.module.production.entity.WorkOrder;
import com.manutrack.module.production.repository.MachineRepository;
import com.manutrack.module.production.repository.WorkOrderRepository;
import com.manutrack.module.iam.entity.User;
import com.manutrack.module.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutomationService {

    private final InventoryItemRepository inventoryItemRepo;
    private final MaterialRequestRepository materialRequestRepo;
    private final WorkOrderRepository workOrderRepo;
    private final MachineRepository machineRepo;
    private final PurchaseOrderRepository purchaseOrderRepo;
    private final InvoiceRepository invoiceRepo;
    private final ShipmentRepository shipmentRepo;
    private final NotificationRepository notificationRepo;
    private final NotificationService notificationService;
    private final UserRepository userRepo;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStockChanged(InventoryItem item) {
        log.info("[AUTO] Stock changed for '{}': {} {} — status: {}",
                item.getDescription(), item.getCurrentStock(), item.getUnitOfMeasure(), item.getStatus());

        if (item.getStatus() == InventoryItem.Status.LOW_STOCK) {
            notifyByRole(
                    "INVENTORY",
                    String.format("⚠ '%s' is LOW STOCK. Current: %.1f %s | Reorder level: %.1f %s. Please initiate procurement.",
                            item.getDescription(), item.getCurrentStock(), item.getUnitOfMeasure(),
                            item.getReorderLevel(), item.getUnitOfMeasure()),
                    Notification.Category.INVENTORY
            );
            notifyByRole(
                    "PROCUREMENT",
                    String.format("⚠ Item '%s' (ID: %d) is LOW STOCK. Reorder required: min %.1f %s",
                            item.getDescription(), item.getItemId(),
                            item.getReorderLevel(), item.getUnitOfMeasure()),
                    Notification.Category.INVENTORY
            );
        }

        if (item.getStatus() == InventoryItem.Status.OUT_OF_STOCK) {
            notifyByRole(
                    "INVENTORY",
                    String.format("🚨 '%s' is OUT OF STOCK! All material requests for this item are now blocked.",
                            item.getDescription()),
                    Notification.Category.INVENTORY
            );
            notifyByRole(
                    "PROCUREMENT",
                    String.format("🚨 URGENT: Item '%s' (ID: %d) is OUT OF STOCK. Auto-reorder PO initiated.",
                            item.getDescription(), item.getItemId()),
                    Notification.Category.INVENTORY
            );
            notifyByRole(
                    "ADMIN",
                    String.format("🚨 '%s' OUT OF STOCK — automatic PO triggered", item.getDescription()),
                    Notification.Category.INVENTORY
            );

            // Auto-raise emergency PO if any active vendor has supplied this item before
            autoRaiseEmergencyPO(item);
        }
    }


    public void updateItemStatus(InventoryItem item) {
        if (item.getCurrentStock() <= 0) {
            item.setCurrentStock(0.0);
            item.setStatus(InventoryItem.Status.OUT_OF_STOCK);
        } else if (item.getCurrentStock() <= item.getReorderLevel()) {
            item.setStatus(InventoryItem.Status.LOW_STOCK);
        } else {
            item.setStatus(InventoryItem.Status.AVAILABLE);
        }
    }




    // PRODUCTION AUTOMATIONS
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onWorkOrderStarted(WorkOrder workOrder) {
        log.info("[AUTO] WorkOrder #{} STARTED — notifying supervisor", workOrder.getWorkOrderId());

        notifyByRole(
                "SUPERVISOR",
                String.format("▶ Work Order WO-%d for product %s has STARTED. Machine: %s.",
                        workOrder.getWorkOrderId(),
                        workOrder.getProductId(),
                        workOrder.getMachine() != null
                                ? workOrder.getMachine().getName() : "Unassigned"),
                Notification.Category.WORK_ORDER
        );
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onWorkOrderCompleted(WorkOrder workOrder) {
        log.info("[AUTO] WorkOrder #{} COMPLETED — running automation", workOrder.getWorkOrderId());

        // 1. Find finished goods inventory item matching this productId and update stock
        inventoryItemRepo.findAll().stream()
                .filter(item -> item.getItemType() == InventoryItem.ItemType.FINISHED_GOOD
                        && item.getDescription().toLowerCase()
                        .contains(workOrder.getProductId().toLowerCase()
                                .replace("PRD-", "")
                                .replace("-", " ")
                                .substring(0, Math.min(4, workOrder.getProductId().length()))
                                .toLowerCase()))
                .findFirst()
                .ifPresent(item -> {
                    double prev = item.getCurrentStock();
                    item.setCurrentStock(prev + workOrder.getQuantity());
                    updateItemStatus(item);
                    inventoryItemRepo.save(item);
                    log.info("[AUTO] Inventory: {} stock {} → {} (added {} units from WO#{})",
                            item.getDescription(), prev, item.getCurrentStock(),
                            workOrder.getQuantity(), workOrder.getWorkOrderId());
                });

        // 2. Notify supervisor
        notifyByRole(
                "SUPERVISOR",
                String.format("✅ Work Order WO-%d for product %s has been COMPLETED. %d units produced.",
                        workOrder.getWorkOrderId(), workOrder.getProductId(), workOrder.getQuantity()),
                Notification.Category.WORK_ORDER
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onWorkOrderHalted(WorkOrder workOrder) {
        log.info("[AUTO] WorkOrder #{} HALTED — notifying supervisor", workOrder.getWorkOrderId());

        notifyByRole(
                "SUPERVISOR",
                String.format("🚨 Work Order WO-%d for product %s has been HALTED. Machine: %s. Immediate attention required.",
                        workOrder.getWorkOrderId(), workOrder.getProductId(),
                        workOrder.getMachine() != null ? workOrder.getMachine().getName() : "Unassigned"),
                Notification.Category.WORK_ORDER
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMachineSetToMaintenance(Machine machine) {
        log.info("[AUTO] Machine #{} → MAINTENANCE — halting linked work orders", machine.getMachineId());

        List<WorkOrder> affected = workOrderRepo.findByMachine_MachineId(machine.getMachineId())
                .stream()
                .filter(wo -> wo.getStatus() == WorkOrder.Status.IN_PROGRESS
                        || wo.getStatus() == WorkOrder.Status.PENDING)
                .toList();

        affected.forEach(wo -> {
            wo.setStatus(WorkOrder.Status.HALTED);
            workOrderRepo.save(wo);
            log.info("[AUTO] WorkOrder WO-{} halted due to machine maintenance", wo.getWorkOrderId());
        });

        notifyByRole(
                "SUPERVISOR",
                String.format("⚙ Machine '%s' (ID: %d) is now under MAINTENANCE. %d work order(s) have been automatically HALTED.",
                        machine.getName(), machine.getMachineId(), affected.size()),
                Notification.Category.MACHINE
        );

        if (!affected.isEmpty()) {
            notifyByRole(
                    "PLANNER",
                    String.format("⚙ Machine '%s' is under maintenance. %d work order(s) halted. Please reschedule.",
                            machine.getName(), affected.size()),
                    Notification.Category.MACHINE
            );
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMachineSetToActive(Machine machine) {
        log.info("[AUTO] Machine #{} → ACTIVE — notifying team", machine.getMachineId());

        notifyByRole(
                "SUPERVISOR",
                String.format("✅ Machine '%s' (ID: %d) is back ACTIVE. Halted work orders can now be resumed.",
                        machine.getName(), machine.getMachineId()),
                Notification.Category.MACHINE
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPlanSetToInProgress(ProductionPlan plan) {
        log.info("[AUTO] Plan '{}' (ID:{}) → IN_PROGRESS — activating work orders & machines",
                plan.getPlanName(), plan.getPlanId());

        List<WorkOrder> pendingOrders = workOrderRepo.findByPlan_PlanId(plan.getPlanId())
                .stream()
                .filter(wo -> wo.getStatus() == WorkOrder.Status.PENDING)
                .toList();

        pendingOrders.forEach(wo -> {
            // 1. Work order → IN_PROGRESS
            wo.setStatus(WorkOrder.Status.IN_PROGRESS);
            if (wo.getActualStart() == null) wo.setActualStart(LocalDateTime.now());
            workOrderRepo.save(wo);
            log.info("[AUTO] WO-{} → IN_PROGRESS (plan started)", wo.getWorkOrderId());

            // 2. Assigned machine → ACTIVE (only if currently IDLE)
            if (wo.getMachine() != null) {
                Machine machine = wo.getMachine();
                if (machine.getStatus() == Machine.Status.IDLE) {
                    machine.setStatus(Machine.Status.ACTIVE);
                    machineRepo.save(machine);
                    log.info("[AUTO] Machine '{}' → ACTIVE (WO-{} started via plan)",
                            machine.getName(), wo.getWorkOrderId());
                }
            }

            // 3. Trigger work-order-started hook
            onWorkOrderStarted(wo);
        });

        // 4. Notify all roles about the plan going live
        notifyAllRoles(
                String.format("▶ Production Plan '%s' (ID: %d) is now IN PROGRESS. %d work order(s) activated.",
                        plan.getPlanName(), plan.getPlanId(), pendingOrders.size()),
                Notification.Category.WORK_ORDER
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPlanSetToCompleted(ProductionPlan plan) {
        log.info("[AUTO] Plan '{}' (ID:{}) → COMPLETED — completing work orders & idling machines",
                plan.getPlanName(), plan.getPlanId());

        List<WorkOrder> activeOrders = workOrderRepo.findByPlan_PlanId(plan.getPlanId())
                .stream()
                .filter(wo -> wo.getStatus() == WorkOrder.Status.IN_PROGRESS
                        || wo.getStatus() == WorkOrder.Status.PENDING)
                .toList();

        activeOrders.forEach(wo -> {
            // 1. Work order → COMPLETED
            wo.setStatus(WorkOrder.Status.COMPLETED);
            if (wo.getActualStart() == null) wo.setActualStart(LocalDateTime.now());
            if (wo.getActualEnd() == null) wo.setActualEnd(LocalDateTime.now());
            workOrderRepo.save(wo);
            log.info("[AUTO] WO-{} → COMPLETED (plan completed)", wo.getWorkOrderId());

            // 2. Assigned machine → IDLE
            if (wo.getMachine() != null) {
                Machine machine = wo.getMachine();
                machine.setStatus(Machine.Status.IDLE);
                machineRepo.save(machine);
                log.info("[AUTO] Machine '{}' → IDLE (WO-{} completed via plan)",
                        machine.getName(), wo.getWorkOrderId());
            }

            // 3. Trigger work-order-completed hook (updates inventory, etc.)
            onWorkOrderCompleted(wo);
        });

        // 4. Notify all roles that the plan is done
        notifyAllRoles(
                String.format("✅ Production Plan '%s' (ID: %d) COMPLETED. %d work order(s) closed. All machines set to IDLE.",
                        plan.getPlanName(), plan.getPlanId(), activeOrders.size()),
                Notification.Category.WORK_ORDER
        );
    }


    // PROCUREMENT AUTOMATIONS

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPurchaseOrderDelivered(PurchaseOrder po) {
        log.info("[AUTO] PO #{} DELIVERED — auto-updating inventory + creating invoice", po.getPoId());

        // 1. Auto-increase inventory stock
        inventoryItemRepo.findById(po.getItemId()).ifPresent(item -> {
            double prev = item.getCurrentStock();
            item.setCurrentStock(prev + po.getQuantity());
            updateItemStatus(item);
            inventoryItemRepo.save(item);
            log.info("[AUTO] Inventory: {} stock {} → {} (PO#{} delivered {} units)",
                    item.getDescription(), prev, item.getCurrentStock(), po.getPoId(), po.getQuantity());

            // Notify inventory manager
            notifyByRole(
                    "INVENTORY",
                    String.format("📦 PO #%d delivered. Stock for '%s' updated: %.1f → %.1f %s. Status: %s",
                            po.getPoId(), item.getDescription(), prev,
                            item.getCurrentStock(), item.getUnitOfMeasure(), item.getStatus()),
                    Notification.Category.INVENTORY
            );
        });

        // 2. Auto-create invoice if not already existing
        boolean invoiceExists = !invoiceRepo.findByPurchaseOrder_PoId(po.getPoId()).isEmpty();
        if (!invoiceExists) {
            Invoice invoice = Invoice.builder()
                    .purchaseOrder(po)
                    .amount(po.getQuantity() * po.getUnitPrice())
                    .issueDate(LocalDate.now())
                    .dueDate(LocalDate.now().plusDays(30))
                    .status(Invoice.Status.OVERDUE)
                    .build();
            invoiceRepo.save(invoice);
            log.info("[AUTO] Invoice auto-created for PO #{}: ${}", po.getPoId(), invoice.getAmount());

            notifyByRole(
                    "PROCUREMENT",
                    String.format("🧾 Invoice auto-created for PO #%d (Vendor: %s). Amount: $%.2f. Due: %s",
                            po.getPoId(), po.getVendor().getName(),
                            invoice.getAmount(), invoice.getDueDate()),
                    Notification.Category.PROCUREMENT
            );
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onInvoiceOverdue(Invoice invoice) {
        log.info("[AUTO] Invoice #{} OVERDUE — notifying procurement", invoice.getInvoiceId());

        notifyByRole(
                "PROCUREMENT",
                String.format("🚨 Invoice #%d from vendor '%s' is OVERDUE. Amount: $%.2f. Was due: %s. Please arrange payment immediately.",
                        invoice.getInvoiceId(),
                        invoice.getPurchaseOrder().getVendor().getName(),
                        invoice.getAmount(),
                        invoice.getDueDate()),
                Notification.Category.PROCUREMENT
        );

        notifyByRole(
                "ADMIN",
                String.format("🚨 Invoice #%d is OVERDUE — $%.2f to %s",
                        invoice.getInvoiceId(), invoice.getAmount(),
                        invoice.getPurchaseOrder().getVendor().getName()),
                Notification.Category.PROCUREMENT
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onVendorRatingLow(Vendor vendor) {
        log.info("[AUTO] Vendor '{}' (ID:{}) has a LOW rating — notifying procurement",
                vendor.getName(), vendor.getVendorId());

        notifyByRole(
                "PROCUREMENT",
                String.format("⚠ Vendor '%s' (ID: %d) has a LOW performance rating. Consider reviewing or replacing this supplier.",
                        vendor.getName(), vendor.getVendorId()),
                Notification.Category.PROCUREMENT
        );

        notifyByRole(
                "ADMIN",
                String.format("⚠ Vendor '%s' flagged for LOW rating. Procurement team notified.",
                        vendor.getName()),
                Notification.Category.PROCUREMENT
        );
    }


    // INVENTORY AUTOMATIONS



    private void autoRaiseEmergencyPO(InventoryItem item) {
        purchaseOrderRepo.findAll().stream()
                .filter(po -> po.getItemId().equals(item.getItemId())
                        && po.getVendor().getStatus() == Vendor.Status.ACTIVE)
                .max(java.util.Comparator.comparing(PurchaseOrder::getOrderDate))
                .ifPresent(lastPO -> {
                    boolean openPOExists = purchaseOrderRepo.findAll().stream()
                            .anyMatch(po -> po.getItemId().equals(item.getItemId())
                                    && po.getStatus() == PurchaseOrder.Status.OPEN);

                    if (!openPOExists) {
                        double reorderQty = item.getReorderLevel() * 3; // Order 3x reorder level
                        PurchaseOrder emergencyPO = PurchaseOrder.builder()
                                .vendor(lastPO.getVendor())
                                .itemId(item.getItemId())
                                .quantity(reorderQty)
                                .unitPrice(lastPO.getUnitPrice())
                                .orderDate(LocalDate.now())
                                .expectedDeliveryDate(LocalDate.now().plusDays(7))
                                .status(PurchaseOrder.Status.OPEN)
                                .build();
                        purchaseOrderRepo.save(emergencyPO);
                        log.info("[AUTO] Emergency PO auto-raised for '{}': {} {} from vendor '{}'",
                                item.getDescription(), reorderQty, item.getUnitOfMeasure(),
                                lastPO.getVendor().getName());

                        notifyByRole(
                                "PROCUREMENT",
                                String.format("🤖 AUTO PO RAISED: Emergency PO created for '%s' — %.1f %s from %s. Please review and confirm.",
                                        item.getDescription(), reorderQty, item.getUnitOfMeasure(),
                                        lastPO.getVendor().getName()),
                                Notification.Category.PROCUREMENT
                        );
                    }
                });
    }


    // LOGISTICS AUTOMATIONS

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onShipmentDispatched(Shipment shipment) {
        log.info("[AUTO] Shipment #{} → IN_TRANSIT — notifying logistics & inventory",
                shipment.getShipmentId());

        notifyByRole(
                "LOGISTICS",
                String.format("🚛 Shipment #%d to '%s' is now IN_TRANSIT via carrier #%d. Dispatched: %s",
                        shipment.getShipmentId(), shipment.getDestination(),
                        shipment.getCarrier() != null ? shipment.getCarrier().getCarrierId() : 0,
                        shipment.getActualDispatchDate()),
                Notification.Category.SHIPMENT
        );

        notifyByRole(
                "INVENTORY",
                String.format("📤 Shipment #%d dispatched from Warehouse #%d to '%s'. Please verify finished goods stock is updated.",
                        shipment.getShipmentId(), shipment.getOriginWarehouseId(),
                        shipment.getDestination()),
                Notification.Category.SHIPMENT
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onShipmentDelivered(Shipment shipment) {
        log.info("[AUTO] Shipment #{} → DELIVERED", shipment.getShipmentId());

        notifyByRole(
                "LOGISTICS",
                String.format("✅ Shipment #%d to '%s' has been DELIVERED. Delivery date: %s",
                        shipment.getShipmentId(), shipment.getDestination(),
                        shipment.getDeliveryDate()),
                Notification.Category.SHIPMENT
        );

        notifyByRole(
                "ADMIN",
                String.format("✅ Shipment #%d DELIVERED to '%s'",
                        shipment.getShipmentId(), shipment.getDestination()),
                Notification.Category.SHIPMENT
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onShipmentDelayed(Shipment shipment) {
        log.info("[AUTO] Shipment #{} → DELAYED — notifying team", shipment.getShipmentId());

        notifyByRole(
                "LOGISTICS",
                String.format("🚨 Shipment #%d to '%s' is DELAYED. Scheduled: %s. Carrier: %s. Please investigate immediately.",
                        shipment.getShipmentId(), shipment.getDestination(),
                        shipment.getScheduledDate(),
                        shipment.getCarrier() != null ? shipment.getCarrier().getName() : "Unknown"),
                Notification.Category.SHIPMENT
        );

        notifyByRole(
                "ADMIN",
                String.format("🚨 Shipment #%d DELAYED — destination: '%s'",
                        shipment.getShipmentId(), shipment.getDestination()),
                Notification.Category.SHIPMENT
        );

        notifyByRole(
                "PLANNER",
                String.format("⚠ Shipment #%d is DELAYED. This may affect production delivery commitments.",
                        shipment.getShipmentId()),
                Notification.Category.SHIPMENT
        );
    }



    /**
     * Sends a notification to every user whose role matches the given role string.
     * This replaces the old hardcoded getRoleDefaultUserId() approach so that
     * ALL users with that role actually receive the notification.
     */
    private void notifyByRole(String role, String message, Notification.Category category) {
        try {
            // ✅ Convert String to Enum safely
            User.Role roleEnum = User.Role.valueOf(role.toUpperCase());

            // ✅ Fetch users by enum role
            List<User> users = userRepo.findByRole(roleEnum);

            if (users.isEmpty()) {
                log.warn("[AUTO] No users found for role '{}' — notification not sent: {}",
                        role, message.substring(0, Math.min(60, message.length())));
                return;
            }

            for (User user : users) {
                Notification notification = Notification.builder()
                        .userId(user.getUserId())
                        .message(message)
                        .category(category)
                        .status(Notification.Status.UNREAD)
                        .build();

                notificationRepo.save(notification);
            }

            log.info("[AUTO] Notification sent to {} user(s) with role {}: {}",
                    users.size(), role, message.substring(0, Math.min(60, message.length())));

        } catch (IllegalArgumentException e) {
            log.warn("[AUTO] Invalid role '{}' provided — cannot convert to enum", role);
        } catch (Exception e) {
            log.warn("[AUTO] Could not send notification to role {}: {}", role, e.getMessage());
        }
    }

    /**
     * Broadcasts a notification to ALL roles in the system.
     */
    public void notifyAllRoles(String message, Notification.Category category) {
        List.of("ADMIN", "PLANNER", "SUPERVISOR", "INVENTORY", "PROCUREMENT", "LOGISTICS")
                .forEach(role -> notifyByRole(role, message, category));
    }



}

