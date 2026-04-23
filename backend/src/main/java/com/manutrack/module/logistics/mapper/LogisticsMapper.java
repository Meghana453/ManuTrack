package com.manutrack.module.logistics.mapper;

import com.manutrack.module.logistics.dto.LogisticsDtos;
import com.manutrack.module.logistics.entity.Carrier;
import com.manutrack.module.logistics.entity.Route;
import com.manutrack.module.logistics.entity.Shipment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LogisticsMapper {
    LogisticsDtos.CarrierResponse toCarrierResponse(Carrier carrier);
    void updateCarrier(LogisticsDtos.UpdateCarrierRequest req, @MappingTarget Carrier carrier);

    LogisticsDtos.RouteResponse toRouteResponse(Route route);
    void updateRoute(LogisticsDtos.UpdateRouteRequest req, @MappingTarget Route route);

    @Mapping(target = "carrierId", source = "carrier.carrierId")
    @Mapping(target = "carrierName", source = "carrier.name")
    LogisticsDtos.ShipmentResponse toShipmentResponse(Shipment shipment);
    void updateShipment(LogisticsDtos.UpdateShipmentRequest req, @MappingTarget Shipment shipment);
}
