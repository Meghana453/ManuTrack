package com.manutrack.module.inventory.repository;

import com.manutrack.module.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByStatus(InventoryItem.Status status);
    List<InventoryItem> findByItemType(InventoryItem.ItemType type);
    List<InventoryItem> findByCurrentStockLessThanEqualAndStatusNot(Double stock, InventoryItem.Status status);
}
