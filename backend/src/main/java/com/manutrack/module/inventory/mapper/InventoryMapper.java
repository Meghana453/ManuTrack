package com.manutrack.module.inventory.mapper;

import com.manutrack.module.inventory.dto.InventoryDtos;
import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.entity.MaterialRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryMapper {
    InventoryDtos.ItemResponse toItemResponse(InventoryItem item);
    void updateItem(InventoryDtos.UpdateItemRequest req, @MappingTarget InventoryItem item);

    @Mapping(target = "itemId", source = "item.itemId")
    @Mapping(target = "workOrderId", source = "workOrderId")
    @Mapping(target = "itemDescription", source = "item.description")
    InventoryDtos.MaterialRequestResponse toMaterialRequestResponse(MaterialRequest req);
}
