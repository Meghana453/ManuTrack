package com.manutrack.module.inventory.mapper;

import com.manutrack.module.inventory.dto.InventoryDtos;
import com.manutrack.module.inventory.entity.InventoryItem;
import com.manutrack.module.inventory.entity.MaterialRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-06T10:10:51+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryDtos.ItemResponse toItemResponse(InventoryItem item) {
        if ( item == null ) {
            return null;
        }

        InventoryDtos.ItemResponse itemResponse = new InventoryDtos.ItemResponse();

        itemResponse.setItemId( item.getItemId() );
        itemResponse.setItemType( item.getItemType() );
        itemResponse.setDescription( item.getDescription() );
        itemResponse.setUnitOfMeasure( item.getUnitOfMeasure() );
        itemResponse.setCurrentStock( item.getCurrentStock() );
        itemResponse.setReorderLevel( item.getReorderLevel() );
        itemResponse.setWarehouseId( item.getWarehouseId() );
        itemResponse.setStatus( item.getStatus() );
        itemResponse.setCreatedAt( item.getCreatedAt() );

        return itemResponse;
    }

    @Override
    public void updateItem(InventoryDtos.UpdateItemRequest req, InventoryItem item) {
        if ( req == null ) {
            return;
        }

        if ( req.getDescription() != null ) {
            item.setDescription( req.getDescription() );
        }
        if ( req.getUnitOfMeasure() != null ) {
            item.setUnitOfMeasure( req.getUnitOfMeasure() );
        }
        if ( req.getCurrentStock() != null ) {
            item.setCurrentStock( req.getCurrentStock() );
        }
        if ( req.getReorderLevel() != null ) {
            item.setReorderLevel( req.getReorderLevel() );
        }
        if ( req.getWarehouseId() != null ) {
            item.setWarehouseId( req.getWarehouseId() );
        }
        if ( req.getStatus() != null ) {
            item.setStatus( req.getStatus() );
        }
    }

    @Override
    public InventoryDtos.MaterialRequestResponse toMaterialRequestResponse(MaterialRequest req) {
        if ( req == null ) {
            return null;
        }

        InventoryDtos.MaterialRequestResponse materialRequestResponse = new InventoryDtos.MaterialRequestResponse();

        materialRequestResponse.setItemId( reqItemItemId( req ) );
        materialRequestResponse.setWorkOrderId( req.getWorkOrderId() );
        materialRequestResponse.setItemDescription( reqItemDescription( req ) );
        materialRequestResponse.setRequestId( req.getRequestId() );
        materialRequestResponse.setQuantity( req.getQuantity() );
        materialRequestResponse.setRequestedDate( req.getRequestedDate() );
        materialRequestResponse.setStatus( req.getStatus() );
        materialRequestResponse.setCreatedAt( req.getCreatedAt() );

        return materialRequestResponse;
    }

    private Long reqItemItemId(MaterialRequest materialRequest) {
        if ( materialRequest == null ) {
            return null;
        }
        InventoryItem item = materialRequest.getItem();
        if ( item == null ) {
            return null;
        }
        Long itemId = item.getItemId();
        if ( itemId == null ) {
            return null;
        }
        return itemId;
    }

    private String reqItemDescription(MaterialRequest materialRequest) {
        if ( materialRequest == null ) {
            return null;
        }
        InventoryItem item = materialRequest.getItem();
        if ( item == null ) {
            return null;
        }
        String description = item.getDescription();
        if ( description == null ) {
            return null;
        }
        return description;
    }
}
