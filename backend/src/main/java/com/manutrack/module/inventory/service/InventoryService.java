
package com.manutrack.module.inventory.service;

import com.manutrack.automation.AutomationService;
import com.manutrack.exception.BusinessException;
import com.manutrack.exception.ResourceNotFoundException;
import com.manutrack.module.inventory.dto.InventoryDtos;
import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.entity.MaterialRequest;
import com.manutrack.module.inventory.mapper.InventoryMapper;
import com.manutrack.module.inventory.repository.InventoryItemRepository;
import com.manutrack.module.inventory.repository.MaterialRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryItemRepository itemRepo;
    private final MaterialRequestRepository requestRepo;
    private final InventoryMapper mapper;
    private final AutomationService automationService;

    public InventoryService(InventoryItemRepository itemRepo,
                            MaterialRequestRepository requestRepo,
                            InventoryMapper mapper,
                            @Lazy AutomationService automationService) {
        this.itemRepo      = itemRepo;
        this.requestRepo   = requestRepo;
        this.mapper        = mapper;
        this.automationService = automationService;
    }

    // ── Items ─────────────────────────────────────────────

    public InventoryDtos.ItemResponse createItem(InventoryDtos.CreateItemRequest req) {
        InventoryItem item = InventoryItem.builder()
                .itemType(req.getItemType())
                .description(req.getDescription())
                .unitOfMeasure(req.getUnitOfMeasure())
                .currentStock(req.getCurrentStock())
                .reorderLevel(req.getReorderLevel())
                .warehouseId(req.getWarehouseId())
                .build();
        automationService.updateItemStatus(item);
        item = itemRepo.save(item);


        if (item.getStatus() != InventoryItem.Status.AVAILABLE) {
            autoCreateMaterialRequest(item, item.getReorderLevel() * 2,
                    "Auto-generated: Item created with stock below reorder level");
            automationService.onStockChanged(item);
        }
        automationService.notifyAllRoles(
                String.format("📦 New Inventory Item '%s' added | Type: %s | Stock: %.1f %s | Reorder at: %.1f | Warehouse: %d | Status: %s",
                        item.getDescription(), item.getItemType(), item.getCurrentStock(),
                        item.getUnitOfMeasure(), item.getReorderLevel(),
                        item.getWarehouseId() != null ? item.getWarehouseId() : 0, item.getStatus()),
                com.manutrack.module.notification.entity.Notification.Category.INVENTORY);
        return mapper.toItemResponse(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.ItemResponse> getAllItems() {
        return itemRepo.findAll().stream().map(mapper::toItemResponse).toList();
    }

    @Transactional(readOnly = true)
    public InventoryDtos.ItemResponse getItemById(Long id) {
        return mapper.toItemResponse(findItemById(id));
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.ItemResponse> getLowStockItems() {
        return itemRepo.findByStatus(InventoryItem.Status.LOW_STOCK).stream()
                .map(mapper::toItemResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.ItemResponse> getOutOfStockItems() {
        return itemRepo.findByStatus(InventoryItem.Status.OUT_OF_STOCK).stream()
                .map(mapper::toItemResponse).toList();
    }


    public InventoryDtos.ItemResponse updateItem(Long id, InventoryDtos.UpdateItemRequest req) {
        InventoryItem item = findItemById(id);
        InventoryItem.Status prevStatus = item.getStatus();
        mapper.updateItem(req, item);
        automationService.updateItemStatus(item);
        item = itemRepo.save(item);

        // AUTO: If stock updated and status got worse → create material request
        if (req.getCurrentStock() != null && item.getStatus() != prevStatus) {
            if (item.getStatus() == InventoryItem.Status.OUT_OF_STOCK) {
                autoCreateMaterialRequest(item, item.getReorderLevel() * 3,
                        "Auto-generated: Stock manually set to 0 (OUT_OF_STOCK)");
                automationService.onStockChanged(item);
                log.info("[AUTO] Stock set to 0 for '{}' → Material Request auto-created", item.getDescription());
            } else if (item.getStatus() == InventoryItem.Status.LOW_STOCK) {
                autoCreateMaterialRequest(item, item.getReorderLevel() * 2,
                        "Auto-generated: Stock updated below reorder level (LOW_STOCK)");
                automationService.onStockChanged(item);
                log.info("[AUTO] Low stock for '{}' → Material Request auto-created", item.getDescription());
            }
        }
        return mapper.toItemResponse(item);
    }

    /**
     * Adjust stock by +/- quantity.
     * If stock becomes 0 → auto-create URGENT material request.
     * If stock drops below reorder level → auto-create material request.
     */
    public InventoryDtos.ItemResponse adjustStock(Long id, InventoryDtos.AdjustStockRequest req) {
        InventoryItem item = findItemById(id);
        double newStock = item.getCurrentStock() + req.getQuantity();
        if (newStock < 0) {
            throw new BusinessException(
                    "Stock cannot go below zero. Current stock: " + item.getCurrentStock()
                            + " " + item.getUnitOfMeasure());
        }

        InventoryItem.Status prevStatus = item.getStatus();
        item.setCurrentStock(newStock);
        automationService.updateItemStatus(item);
        item = itemRepo.save(item);

        // AUTO: Status worsened → create material request
        if (item.getStatus() != prevStatus) {
            if (item.getStatus() == InventoryItem.Status.OUT_OF_STOCK) {
                autoCreateMaterialRequest(item, item.getReorderLevel() * 3,
                        "Auto-generated: Stock adjusted to 0 — URGENT restock required. Reason: " + req.getReason());
                automationService.onStockChanged(item);
                log.info("[AUTO] OUT_OF_STOCK after adjustment for '{}' → Material Request auto-created",
                        item.getDescription());

            } else if (item.getStatus() == InventoryItem.Status.LOW_STOCK) {
                autoCreateMaterialRequest(item, item.getReorderLevel() * 2,
                        "Auto-generated: Stock dropped below reorder level. Reason: " + req.getReason());
                automationService.onStockChanged(item);
                log.info("[AUTO] LOW_STOCK after adjustment for '{}' → Material Request auto-created",
                        item.getDescription());
            }
        }
        return mapper.toItemResponse(item);
    }

    public void deleteItem(Long id) {
        findItemById(id);
        itemRepo.deleteById(id);
    }

    // ── Material Requests ─────────────────────────────────

    public InventoryDtos.MaterialRequestResponse createMaterialRequest(InventoryDtos.CreateMaterialRequestDto req) {
        InventoryItem item = findItemById(req.getItemId());
        MaterialRequest request = MaterialRequest.builder()
                .workOrderId(req.getWorkOrderId())
                .item(item)
                .quantity(req.getQuantity())
                .requestedDate(req.getRequestedDate() != null ? req.getRequestedDate() : LocalDate.now())
                .status(MaterialRequest.Status.PENDING)
                .build();
        return mapper.toMaterialRequestResponse(requestRepo.save(request));
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.MaterialRequestResponse> getAllMaterialRequests() {
        return requestRepo.findAll().stream().map(mapper::toMaterialRequestResponse).toList();
    }

    @Transactional(readOnly = true)
    public InventoryDtos.MaterialRequestResponse getMaterialRequestById(Long id) {
        return mapper.toMaterialRequestResponse(findRequestById(id));
    }

//    public InventoryDtos.MaterialRequestResponse updateMaterialRequest(Long id,
//                                                                       InventoryDtos.UpdateMaterialRequestDto req) {
//        MaterialRequest request = findRequestById(id);
//        if (req.getQuantity() != null) request.setQuantity(req.getQuantity());
//        if (req.getStatus() != null) {
//            if (req.getStatus() == MaterialRequest.Status.FULFILLED) {
//                InventoryItem item = request.getItem();
//                if (item.getCurrentStock() < request.getQuantity()) {
//                    throw new BusinessException(
//                            "Insufficient stock to fulfill request. Available: "
//                                    + item.getCurrentStock() + " " + item.getUnitOfMeasure());
//                }
//                InventoryItem.Status prevStatus = item.getStatus();
//                item.setCurrentStock(item.getCurrentStock() - request.getQuantity());
//                automationService.updateItemStatus(item);
//                item = itemRepo.save(item);
//                log.info("[AUTO] Stock deducted for material request #{}: {} {} from '{}'",
//                        id, request.getQuantity(), item.getUnitOfMeasure(), item.getDescription());
//
//                // If fulfilling caused stock to drop further → create new request
//                if (item.getStatus() != prevStatus && item.getStatus() != InventoryItem.Status.AVAILABLE) {
//                    autoCreateMaterialRequest(item, item.getReorderLevel() * 2,
//                            "Auto-generated: Stock dropped after fulfilling request #" + id);
//                    automationService.onStockChanged(item);
//                }
//            }
//            request.setStatus(req.getStatus());
//        }
//        return mapper.toMaterialRequestResponse(requestRepo.save(request));
//    }
public InventoryDtos.MaterialRequestResponse updateMaterialRequest(
        Long id,
        InventoryDtos.UpdateMaterialRequestDto req) {

    MaterialRequest existing = findRequestById(id);

    // ✅ Update scalar fields
    if (req.getRequestedDate() != null)
        existing.setRequestedDate(req.getRequestedDate());

    if (req.getWorkOrderId() != null)
        existing.setWorkOrderId(req.getWorkOrderId());

    if (req.getQuantity() != null)
        existing.setQuantity(req.getQuantity());

    // ✅ Update item reference only if changed
    if (req.getItemId() != null &&
            (existing.getItem() == null ||
                    !req.getItemId().equals(existing.getItem().getItemId()))) {

        var itemRef = itemRepo.getReferenceById(req.getItemId());
        existing.setItem(itemRef);
    }

    // ✅ Status update with stock deduction logic
    if (req.getStatus() != null) {
        MaterialRequest.Status newStatus = req.getStatus();

        // Only when fulfilling a request
        if (newStatus == MaterialRequest.Status.FULFILLED) {

            InventoryItem item = existing.getItem();

            if (item.getCurrentStock() < existing.getQuantity()) {
                throw new BusinessException(
                        "Insufficient stock to fulfill request. Available: "
                                + item.getCurrentStock() + " " + item.getUnitOfMeasure());
            }

            InventoryItem.Status prevStatus = item.getStatus();
            item.setCurrentStock(item.getCurrentStock() - existing.getQuantity());
            automationService.updateItemStatus(item);
            itemRepo.save(item);

            // Auto-create new MR if status worsened
            if (item.getStatus() != prevStatus &&
                    item.getStatus() != InventoryItem.Status.AVAILABLE) {

                autoCreateMaterialRequest(item,
                        item.getReorderLevel() * 2,
                        "Auto-generated: Stock dropped after fulfilling request #" + id);

                automationService.onStockChanged(item);
            }
        }

        existing.setStatus(newStatus);
    }

    MaterialRequest saved = requestRepo.save(existing);
    return mapper.toMaterialRequestResponse(saved);
}

    public void deleteMaterialRequest(Long id) {
        findRequestById(id);
        requestRepo.deleteById(id);
    }



    /**
     * Auto-creates a PENDING MaterialRequest for an item that needs restocking.
     * Skips creation if an identical PENDING request already exists for the same item.
     */
    private void autoCreateMaterialRequest(InventoryItem item, double quantity, String reason) {

        boolean pendingExists = requestRepo.findByItem_ItemId(item.getItemId())
                .stream()
                .anyMatch(r -> r.getStatus() == MaterialRequest.Status.PENDING);

        if (pendingExists) {
            log.info("[AUTO] Skipping duplicate material request for '{}' — PENDING request already exists",
                    item.getDescription());
            return;
        } else {
            // Quantity must be at least 1
            double requestQty = Math.max(quantity, 1.0);

            MaterialRequest autoRequest = MaterialRequest.builder()
                    .item(item)
                    .quantity(requestQty)
                    .requestedDate(LocalDate.now())
                    .status(MaterialRequest.Status.PENDING)
                    .build();
            requestRepo.save(autoRequest);

            log.info("[AUTO] Material Request created for '{}' — Qty: {} {} | Reason: {}",
                    item.getDescription(), requestQty, item.getUnitOfMeasure(), reason);
        }
    }

    private InventoryItem findItemById(Long id) {
        return itemRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", "id", id));
    }

    private MaterialRequest findRequestById(Long id) {
        return requestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaterialRequest", "id", id));
    }
}
