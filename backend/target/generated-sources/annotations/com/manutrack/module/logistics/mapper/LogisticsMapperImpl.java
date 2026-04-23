package com.manutrack.module.logistics.mapper;

import com.manutrack.module.logistics.dto.LogisticsDtos;
import com.manutrack.module.logistics.entity.Carrier;
import com.manutrack.module.logistics.entity.Route;
import com.manutrack.module.logistics.entity.Shipment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-26T18:22:21+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class LogisticsMapperImpl implements LogisticsMapper {

    @Override
    public LogisticsDtos.CarrierResponse toCarrierResponse(Carrier carrier) {
        if ( carrier == null ) {
            return null;
        }

        LogisticsDtos.CarrierResponse carrierResponse = new LogisticsDtos.CarrierResponse();

        carrierResponse.setCarrierId( carrier.getCarrierId() );
        carrierResponse.setName( carrier.getName() );
        carrierResponse.setContactInfo( carrier.getContactInfo() );
        carrierResponse.setRating( carrier.getRating() );
        carrierResponse.setStatus( carrier.getStatus() );
        carrierResponse.setCreatedAt( carrier.getCreatedAt() );

        return carrierResponse;
    }

    @Override
    public void updateCarrier(LogisticsDtos.UpdateCarrierRequest req, Carrier carrier) {
        if ( req == null ) {
            return;
        }

        if ( req.getName() != null ) {
            carrier.setName( req.getName() );
        }
        if ( req.getContactInfo() != null ) {
            carrier.setContactInfo( req.getContactInfo() );
        }
        if ( req.getRating() != null ) {
            carrier.setRating( req.getRating() );
        }
        if ( req.getStatus() != null ) {
            carrier.setStatus( req.getStatus() );
        }
    }

    @Override
    public LogisticsDtos.RouteResponse toRouteResponse(Route route) {
        if ( route == null ) {
            return null;
        }

        LogisticsDtos.RouteResponse routeResponse = new LogisticsDtos.RouteResponse();

        routeResponse.setRouteId( route.getRouteId() );
        routeResponse.setOrigin( route.getOrigin() );
        routeResponse.setDestination( route.getDestination() );
        routeResponse.setDistance( route.getDistance() );
        routeResponse.setEstimatedTimeHours( route.getEstimatedTimeHours() );
        routeResponse.setCreatedAt( route.getCreatedAt() );

        return routeResponse;
    }

    @Override
    public void updateRoute(LogisticsDtos.UpdateRouteRequest req, Route route) {
        if ( req == null ) {
            return;
        }

        if ( req.getOrigin() != null ) {
            route.setOrigin( req.getOrigin() );
        }
        if ( req.getDestination() != null ) {
            route.setDestination( req.getDestination() );
        }
        if ( req.getDistance() != null ) {
            route.setDistance( req.getDistance() );
        }
        if ( req.getEstimatedTimeHours() != null ) {
            route.setEstimatedTimeHours( req.getEstimatedTimeHours() );
        }
    }

    @Override
    public LogisticsDtos.ShipmentResponse toShipmentResponse(Shipment shipment) {
        if ( shipment == null ) {
            return null;
        }

        LogisticsDtos.ShipmentResponse shipmentResponse = new LogisticsDtos.ShipmentResponse();

        shipmentResponse.setCarrierId( shipmentCarrierCarrierId( shipment ) );
        shipmentResponse.setCarrierName( shipmentCarrierName( shipment ) );
        shipmentResponse.setShipmentId( shipment.getShipmentId() );
        shipmentResponse.setOriginWarehouseId( shipment.getOriginWarehouseId() );
        shipmentResponse.setDestination( shipment.getDestination() );
        shipmentResponse.setScheduledDate( shipment.getScheduledDate() );
        shipmentResponse.setActualDispatchDate( shipment.getActualDispatchDate() );
        shipmentResponse.setDeliveryDate( shipment.getDeliveryDate() );
        shipmentResponse.setStatus( shipment.getStatus() );
        shipmentResponse.setCreatedAt( shipment.getCreatedAt() );

        return shipmentResponse;
    }

    @Override
    public void updateShipment(LogisticsDtos.UpdateShipmentRequest req, Shipment shipment) {
        if ( req == null ) {
            return;
        }

        if ( req.getDestination() != null ) {
            shipment.setDestination( req.getDestination() );
        }
        if ( req.getScheduledDate() != null ) {
            shipment.setScheduledDate( req.getScheduledDate() );
        }
        if ( req.getActualDispatchDate() != null ) {
            shipment.setActualDispatchDate( req.getActualDispatchDate() );
        }
        if ( req.getDeliveryDate() != null ) {
            shipment.setDeliveryDate( req.getDeliveryDate() );
        }
        if ( req.getStatus() != null ) {
            shipment.setStatus( req.getStatus() );
        }
    }

    private Long shipmentCarrierCarrierId(Shipment shipment) {
        if ( shipment == null ) {
            return null;
        }
        Carrier carrier = shipment.getCarrier();
        if ( carrier == null ) {
            return null;
        }
        Long carrierId = carrier.getCarrierId();
        if ( carrierId == null ) {
            return null;
        }
        return carrierId;
    }

    private String shipmentCarrierName(Shipment shipment) {
        if ( shipment == null ) {
            return null;
        }
        Carrier carrier = shipment.getCarrier();
        if ( carrier == null ) {
            return null;
        }
        String name = carrier.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
